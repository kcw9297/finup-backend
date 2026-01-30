package app.finup.layer.domain.news.service;

import app.finup.api.external.news.enums.NewsSortType;
import app.finup.common.enums.LogEmoji;
import app.finup.common.utils.*;
import app.finup.api.external.news.dto.NewsApi;
import app.finup.api.external.news.client.NewsClient;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.enums.NewsType;
import app.finup.layer.domain.news.repository.NewsRepository;
import app.finup.layer.domain.news.support.NewsObject;
import app.finup.layer.domain.news.utils.NewsCrawlingUtils;
import app.finup.layer.domain.news.utils.NewsFilterUtils;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.redis.StockRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.Stream;

/**
 * NewsSchedulerService 구현 클래스
 * @author kcw
 * @since 2025-12-31
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsSchedulerServiceImpl implements NewsSchedulerService {

    // 사용 의존성
    private final NewsRepository newsRepository;
    private final NewsClient newsClient;
    private final StockRedisStorage stockRedisStorage;

    // 병렬 로직을 제어할 의존성
    private final ExecutorService newsApiExecutor;
    private final ExecutorService crawlingExecutor;

    // 사용 상수
    private static final String URL_NAVER_NEWS = "https://n.news.naver.com";
    private static final String QUERY_SEARCH_MAIN = "국내 증시 코스피 전망 기관";
    private static final Duration THRESHOLD_SAVE = Duration.ofDays(30); // 약 1달 정도 기사까진 보존
    private static final int AMOUNT_NEWS_MAIN = 100; // 검색 기사 수량
    private static final int AMOUNT_NEWS_STOCK = 50; // 검색 기사 수량
    private static final Duration WAIT_STOCK_SYNC = Duration.ofMillis(2000); // 대기 시간


    @CacheEvict( // 기존 캐시 삭제
            value = NewsRedisKey.CACHE_MAIN,
            allEntries = true
    )
    @Override
    public void syncMain() {

        // [1] 현재 뉴스 엔티티 조회
        List<News> curNews = newsRepository.findByNewsType(NewsType.MAIN);

        // [2] 뉴스 검색 수행 및 필터링 결과 기반 엔티티 생성 및 저장
        // 메인 뉴스 조회
        List<NewsApi.Row> news = newsClient.getLatest(QUERY_SEARCH_MAIN, AMOUNT_NEWS_MAIN, NewsSortType.LATEST);
        LogUtils.showInfo(this.getClass(), LogEmoji.TRY, "메인 뉴스 API 검색 완료: [%,d]개", news.size());

        // 검색 결과 필터링
        List<NewsApi.Row> filteredNews = NewsFilterUtils.filterDetailTitle(news, curNews, NewsFilterUtils.FILTER_KEYWORD_MAIN);
        LogUtils.showInfo(this.getClass(), LogEmoji.TRY, "메인 뉴스 필터링 완료: [%,d]개", filteredNews.size());

        // [3] 필터된 결과 기반 Entity 생성 및 저장
        int saved = processCrawlingAndSave(
                "메인 뉴스 크롤링 및 저장",
                filteredNews,  //  필터링된 크롤링 요청을 직접 사용
                this::toMainNewsEntity,
                curNews
        );

        // [4] 저장 결과 로그 출력
        LogUtils.showInfo(this.getClass(), LogEmoji.OK,
                "메인 뉴스 동기화 완료 - 검색된 뉴스: [%,d]개, 제목 필터 후 뉴스: [%,d]개, 저장 성공 뉴스: [%,d]개",
                news.size(), filteredNews.size(), saved
        );
    }


    @CacheEvict(
            value = NewsRedisKey.CACHE_STOCK,
            allEntries = true  // 모든 종목 뉴스 캐시 삭제
    )
    @Override
    public void syncStock() {

        // [1] 현재 주식정보 조회
        List<StockDto.Info> stockInfos = stockRedisStorage.getAllStockInfos();

        // [2] 현재 뉴스 조회
        List<News> curNews = new ArrayList<>(newsRepository.findByNewsType(NewsType.STOCK));
        Map<String, List<News>> curEntityMap = curNews.stream()
                .collect(Collectors.groupingBy(News::getStockCode));

        // [3] API 호출 및 제목 필터링
        List<StockNewsRequest> requests = stockInfos.stream()
                .map(info -> new StockNewsRequest(
                        info.getDetail().getStockCode(),
                        info.getDetail().getStockName()
                ))
                .toList();


        // [4] 뉴스 API 호출 > 크롤링 > DB저장 로직 일괄 수행 (종목 코드 별)
        AtomicInteger totalSearched = new AtomicInteger(0);
        AtomicInteger totalTitleFiltered = new AtomicInteger(0);
        AtomicInteger totalSaved = new AtomicInteger(0);
        AtomicInteger successStocks = new AtomicInteger(0);
        AtomicInteger failedStocks = new AtomicInteger(0);
        AtomicInteger processedStocks = new AtomicInteger(0);
        int tryStocks = requests.size();

        // [5] 종목별 순차 처리 (예외 격리)
        requests.forEach(request -> {
            try {
                // 동기화 수행 후 결과 통계정보 얻어옴
                SyncStockNewsResponse response =
                        processSyncSingleStock(request, curEntityMap, totalSearched, totalTitleFiltered, totalSaved);

                // 성공 카운팅 후 로그 출력
                successStocks.incrementAndGet();
                LogUtils.showInfo(this.getClass(), LogEmoji.OK,
                        "[%d/%d] 종목 [%s(%s)] 처리 완료. 검색: [%,d]개, 필터링: [%,d]개, 저장: [%,d]개",
                        processedStocks.incrementAndGet(), tryStocks, request.stockName, request.stockCode,
                        response.apiSearchAmount, response.afterFilteredAmount, response.savedAmount
                );

            } catch (Exception e) {
                failedStocks.incrementAndGet();
                LogUtils.showWarn(this.getClass(),
                        "[%d/%d] 종목 [%s(%s)] 처리 실패.  원인: %s",
                        processedStocks.incrementAndGet(), tryStocks, request.stockName, request.stockCode, e.getMessage()
                );

                // 완료 후 일정 시간 대기
            } finally {
                ParallelUtils.wait(WAIT_STOCK_SYNC, this.getClass());
            }
        });

        // [6] 최종 결과 로그
        LogUtils.showInfo(this.getClass(), LogEmoji.OK,
                "종목 뉴스 동기화 완료 - 총 종목: [%d]개, 실패 종목: [%d]개, 검색된 뉴스: [%,d]개, 제목 필터 후 뉴스: [%,d]개, 저장 성공 뉴스: [%,d]개",
                requests.size(), failedStocks.get(),
                totalSearched.get(), totalTitleFiltered.get(), totalSaved.get()
        );
    }

    // 단일 종목 처리
    private SyncStockNewsResponse processSyncSingleStock(
            StockNewsRequest request,
            Map<String, List<News>> curEntityMap,
            AtomicInteger totalSearched,
            AtomicInteger totalTitleFiltered,
            AtomicInteger totalSaved
    ) {

        // [1] API 조회
        String stockCode = request.stockCode;
        String stockName = request.stockName;

        List<NewsApi.Row> apiResults = newsClient.getLatest(
                stockName,
                AMOUNT_NEWS_STOCK,
                NewsSortType.RELATED
        );
        totalSearched.addAndGet(apiResults.size());

        // [2] 제목 필터링
        List<News> curStockNews = curEntityMap.getOrDefault(stockCode, List.of());
        List<NewsApi.Row> filtered = NewsFilterUtils.filterDetailTitle(
                apiResults,
                curStockNews,
                NewsFilterUtils.FILTER_KEYWORD_STOCK
        );
        totalTitleFiltered.addAndGet(filtered.size());

        // [3] 크롤링 및 저장
        int saved = processCrawlingAndSave(
                "종목 뉴스 크롤링 및 저장",
                filtered,
                stockNews -> toStockNewsEntity(stockNews, stockCode),
                curStockNews
        );
        totalSaved.addAndGet(saved);

        // [4] 현재 종목에 대한 통계 전달
        return new SyncStockNewsResponse(apiResults.size(), filtered.size(), saved);
    }


    // 배치 단위로 크롤링 및 저장 수행
    private <T extends NewsObject> int processCrawlingAndSave(
            String taskName,
            List<T> news,
            Function<T, News> crawler,
            List<News> currentNews
    ) {
        try {
            // [1] 처리할 정보가 없는지 검증
            if (news.isEmpty()) return 0; // 처리 개수가 없으므로 0 반환 후 중단

            // [2] 크롤링 수행 및 결과 본문 유사도 비교
            List<News> crawledNews = doCrawlingStockNewsAndMapToEntity(taskName, news, crawler);
            List<News> filteredNews = NewsFilterUtils.filterSimilarDescription(crawledNews, currentNews, false);

            // [3] 저장 수행
            if (!filteredNews.isEmpty()) newsRepository.saveAll(filteredNews);

            // [4] 저장된 엔티티 개수 반환
            return filteredNews.size();

        } catch (Exception e) {
            LogUtils.showWarn(this.getClass(), "%s 크롤링 처리 실패. 시도 개수 [%,d]개. 원인 : %s", taskName, news.size(), e.getMessage());
            return 0; // 0 반환 (정상 흐름 처리)
        }

    }


    // 크롤링 수행 및 크롤링 결과 기사 정보를 기사 엔티티로 변경
    private <T extends NewsObject> List<News> doCrawlingStockNewsAndMapToEntity(
            String message,
            List<T> crawlingRequest,
            Function<T, News> crawler
    ) {

        // 먼저 네이버 뉴스는 잦은 요청 시 429(Too Many Request)가 발생하므로, 해당 요청은 분리하여 처리
        List<T> naverNewsRequest = new ArrayList<>(); // 네이버 뉴스 요청
        List<T> etcNewsRequest = new ArrayList<>(); // 그 외 뉴스 요청

        // 네이버 / 그 외 기타 기사를 구분하여 저장
        crawlingRequest
                .forEach(request -> {

                    // 네이버 뉴스 요청인 경우
                    if (request.getLink().startsWith(URL_NAVER_NEWS))
                        naverNewsRequest.add(request);

                    // 그 외 뉴스 요청인 경우
                    else etcNewsRequest.add(request);
                });

        // 크롤링 수행 후, 결과 필터링
        return doCrawlingStockNews(message, naverNewsRequest, etcNewsRequest, crawler)
                .filter(Objects::nonNull)
                .filter(entity -> NewsFilterUtils.isDescriptionValid(entity.getDescription()))
                .toList();
    }

    // 종목 뉴스 크롤링 수행 처리 메소드
    private <T extends NewsObject> Stream<News> doCrawlingStockNews(
            String message,
            List<T> naverNewsRequest,
            List<T> etcNewsRequest,
            Function<T, News> crawler
    ) {

        // [1] 병렬 처리를 콜렉션 선언
        List<Supplier<List<News>>> tasks = new ArrayList<>();
        tasks.add(() -> ParallelUtils.doParallelTask(
                "네이버 뉴스 크롤링", naverNewsRequest, crawler, ParallelUtils.SEMAPHORE_NEWS_CRAWLING_NAVER, crawlingExecutor, Duration.ofMillis(1500)
        ));
        tasks.add(() -> ParallelUtils.doParallelTask(
                "기타 뉴스 크롤링", etcNewsRequest, crawler, ParallelUtils.SEMAPHORE_NEWS_CRAWLING_ETC, crawlingExecutor, Duration.ofMillis(200)
        ));

        // [2] 두 뉴스 크롤링 요청을 각각 병렬 처리
        return ParallelUtils.doParallelTask(
                message,
                tasks,
                Supplier::get,
                ParallelUtils.SEMAPHORE_UNLIMITED,
                crawlingExecutor

        ).stream().flatMap(List::stream);
    }


    // 메인 뉴스 Entity 클래스로 변환
    private News toMainNewsEntity(NewsApi.Row row) {

        // 크롤링 수행
        News.NewsBuilder newsBuilder = crawlAndSetNewsEntity(row);

        // 크롤링 결과에 따라 반환
        return Objects.isNull(newsBuilder) ?
                null :
                newsBuilder
                        .newsType(NewsType.MAIN)
                        .build();
    }

    // 종목 뉴스 Entity 클래스로 변환
    private News toStockNewsEntity(NewsApi.Row row, String stockCode) {

        // 크롤링 수행
        News.NewsBuilder newsBuilder = crawlAndSetNewsEntity(row);

        // 크롤링 결과에 따라 반환
        return Objects.isNull(newsBuilder) ?
                null :
                newsBuilder
                        .newsType(NewsType.STOCK)
                        .stockCode(stockCode)
                        .build();
    }

    // 기본 값 설정
    private News.NewsBuilder crawlAndSetNewsEntity(NewsApi.Row row) {

        // 기사 링크
        String link = row.getLink();

        // 기사 크롤링 수행
        NewsDto.CrawlResult result = NewsCrawlingUtils.extractAll(link);

        // 뉴스 엔티티 생성 및 반환
        return result.isSuccess() ?
                News.builder()
                        .title(HtmlUtils.getText(row.getTitle()))
                        .summary(HtmlUtils.getText(row.getSummary()))
                        .description(NewsFilterUtils.removeDescriptionNoiseLine(result.getDescription()))
                        .thumbnail(result.getThumbnail())
                        .publisher(result.getPublisher())
                        .link(link)
                        .publishedAt(row.getPublishedAt()) : null;
    }

    // 종목 검색 작업 요청 DTO
    private record StockNewsRequest (String stockCode, String stockName) {}
    private record SyncStockNewsResponse(int apiSearchAmount, int afterFilteredAmount, int savedAmount) {}


    @Override
    public void clean() {
        int removedAmount = newsRepository.removeOld(TimeUtils.getNowLocalDateTime().minus(THRESHOLD_SAVE));
        LogUtils.showInfo(this.getClass(), "오래된 뉴스 삭제. 삭제 뉴스 개수 : %,d", removedAmount);
    }

}
