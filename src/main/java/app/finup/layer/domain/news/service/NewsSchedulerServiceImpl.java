package app.finup.layer.domain.news.service;

import app.finup.api.external.news.enums.NewsSortType;
import app.finup.common.utils.HtmlUtils;
import app.finup.common.utils.ParallelUtils;
import app.finup.api.external.news.dto.NewsApi;
import app.finup.api.external.news.client.NewsClient;
import app.finup.common.utils.TimeUtils;
import app.finup.layer.domain.news.constant.NewsFilter;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.entity.News;
import app.finup.layer.domain.news.enums.NewsType;
import app.finup.layer.domain.news.repository.NewsRepository;
import app.finup.layer.domain.news.utils.NewsCrawlingUtils;
import app.finup.layer.domain.news.utils.NewsFilterUtils;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.redis.StockRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

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
    private static final String QUERY_SEARCH_MAIN = "국내 증시 코스피 전망 기관";
    private static final Duration THRESHOLD_SAVE = Duration.ofDays(14); // 작성된 지 2주가 지난 기사는 제거
    private static final int AMOUNT_NEWS_MAIN = 100; // 검색 기사 수량
    private static final int AMOUNT_NEWS_STOCK = 50; // 검색 기사 수량



    @CacheEvict( // 기존 캐시 삭제
            value = NewsRedisKey.CACHE_MAIN,
            allEntries = true
    )
    @Override
    public void syncMain() {

        // [1] 현재 뉴스 엔티티 조회
        Map<String, News> curTitleEntityMap = // "뉴스 제목" - "News Entity" Map (뉴스 이름으로 중복 판단)
                newsRepository.findByNewsType(NewsType.MAIN)
                        .stream()
                        .collect(Collectors.toConcurrentMap(
                                news -> normalizeTitle(news.getTitle()),
                                Function.identity(),
                                (existing, replacement) -> existing
                        ));

        // [2] 뉴스 검색 수행 및 필터링 결과 기반 엔티티 생성 및 저장
        // API 뉴스 기사 및 현재 기사 목록 일괄 조회 후, 유사도 및 중복 기사 필터링
        List<NewsApi.Row> rows = newsClient.getLatest(QUERY_SEARCH_MAIN, AMOUNT_NEWS_MAIN, NewsSortType.LATEST);
        List<NewsApi.Row> filteredRows = NewsFilterUtils.filter(rows, NewsFilter.FILTER_KEYWORD_MAIN);

        // [3] 필터된 결과 기반 Entity 생성 및 저장
        List<News> entities =
                filteredRows.parallelStream()
                        .filter(dto -> isNotDuplicatedTitle(dto, curTitleEntityMap))
                        .map(this::crawlNewsAndMapToNewsEntity)
                        .filter(Objects::nonNull)
                        .filter(entity -> NewsFilterUtils.isDescriptionValid(entity.getDescription()))
                        .toList();

        // [4] 저장 수행 (내부 트랜잭션 사용)
        newsRepository.saveAll(entities);
    }


    @CacheEvict(
            value = NewsRedisKey.CACHE_STOCK,
            allEntries = true  // 모든 종목 뉴스 캐시 삭제
    )
    @Override
    public void syncStock() {

        // [1] 현재 주식정보 조회
        List<StockDto.Info> stockInfos = stockRedisStorage.getAllStockInfos();

        // [2] 현재 뉴스 엔티티 조회
        List<News> curNews = newsRepository.findByNewsType(NewsType.STOCK);

        // Set<뉴스제목>
        Set<String> curTitles = curNews.stream()
                .map(entity -> normalizeTitle(entity.getTitle()))
                .collect(Collectors.toSet());

        // Map<종목코드, Map<뉴스제목, News>>
        Map<String, Map<String, News>> curEntityMap =
                curNews.stream()
                .collect(Collectors.groupingBy(
                        News::getStockCode,  // 종목코드로 그룹화
                        Collectors.toMap(
                                news -> normalizeTitle(news.getTitle()),
                                Function.identity(),
                                (existing, replacement) -> existing
                        )
                ));


        // [3] 모든 종목 별 뉴스 조회
        // 종목명 추출
        List<StockNewsRequest> requests = stockInfos.stream()
                .map(info -> new StockNewsRequest(
                        info.getDetail().getStockCode(),
                        info.getDetail().getStockName()
                ))
                .toList();

        // API 검색 수행
        Map<String, List<NewsApi.Row>> responses = ParallelUtils.doParallelTask(
                "네이버 뉴스 검색 API 호출",
                requests,
            request -> new StockNewsResponse(newsClient.getLatest(request.stockName, AMOUNT_NEWS_STOCK, NewsSortType.RELATED), request.stockCode(), request.stockName()),
                ParallelUtils.SEMAPHORE_API_NAVER_NEWS,
                newsApiExecutor
        ).stream()
            .map(response -> // 검색 결과 필터링
                    Map.entry(response.stockCode, NewsFilterUtils.filter(response.stocks, NewsFilter.FILTER_KEYWORD_STOCK))
            )
            //.peek(response -> log.warn("추출 기사 Code : {}, 기사수 : {}", response.getKey(), response.getValue().size()))
            .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));


        // [4] 검색 수행결과 기반, 본문 크롤링을 수행할 대상 추출
        List<StockNewsCrawRequest> crawlingRequest = responses.entrySet()
                .stream()
                .flatMap(entry -> {
                    String stockCode = entry.getKey();
                    List<NewsApi.Row> rows = entry.getValue();
                    Map<String, News> existingNews = curEntityMap.getOrDefault(stockCode, Map.of());

                    // 중복 제거된 row만 크롤링 대상으로
                    return rows.stream()
                            .filter(row -> isNotDuplicatedTitle(row, existingNews))
                            .map(row -> new StockNewsCrawRequest(stockCode, row));
                })
                .toList();

        // [5] 병렬 크롤링 및 Entity 생성
        List<News> entities = ParallelUtils.doParallelTask(
                        "네이버 뉴스 검색 결과 크롤링",
                        crawlingRequest,
                        request -> toStockNewsEntity(request.row(), request.stockCode()),
                        ParallelUtils.SEMAPHORE_NEWS_CRAWLING,
                        crawlingExecutor,
                        Duration.ofMillis(500))
                .stream()
                .filter(Objects::nonNull)
                .filter(entity -> NewsFilterUtils.isDescriptionValid(entity.getDescription()))
                .filter(entity -> isNotDuplicatedTitle(entity, curTitles)) // 종목마다 중복 기사가 존재할 수 있으므로 한번 더 수행
                .toList();

        // [6] 저장 (내부 트랜잭션 사용)
        newsRepository.saveAll(entities);
    }


    // 기존 기사와 중복되는 제목인지 판별
    private boolean isNotDuplicatedTitle(NewsApi.Row dto, Map<String, News> curEntityMap) {
        String title = normalizeTitle(HtmlUtils.getText(dto.getTitle()));
        return !curEntityMap.containsKey(title);
    }

    private boolean isNotDuplicatedTitle(News entity, Set<String> curTitles) {
        String title = normalizeTitle(HtmlUtils.getText(entity.getTitle()));
        return !curTitles.contains(title);
    }


    // 제목 정규화 (띄어쓰기, 특수문자 제거)
    private String normalizeTitle(String title) {

        if (Objects.isNull(title)) return "";

        return title
                .replaceAll("\\s+", "")  // 모든 공백 제거 (띄어쓰기, 탭, 줄바꿈 등)
                .replaceAll("[.]{2,}", "") // ".." 이상의 점 제거
                .replaceAll("[^가-힣a-zA-Z0-9]", "")
                .toLowerCase();  // 대소문자 통일 // 한글, 영문, 숫자만 남김
    }


    // 메인 뉴스 Entity 클래스로 변환
    private News crawlNewsAndMapToNewsEntity(NewsApi.Row row) {

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
    private record StockNewsRequest(String stockCode, String stockName) {}
    private record StockNewsResponse(List<NewsApi.Row> stocks, String stockCode, String stockName) {}
    private record StockNewsCrawRequest(String stockCode, NewsApi.Row row) {}


    @Override
    @Transactional
    public void removeOlds() {
        newsRepository.removeOld(TimeUtils.getNowLocalDateTime().minus(THRESHOLD_SAVE));
    }

}
