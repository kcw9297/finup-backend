package app.finup.layer.domain.news.service;

import app.finup.common.utils.HtmlUtils;
import app.finup.common.utils.ParallelUtils;
import app.finup.api.external.news.dto.NewsApi;
import app.finup.api.external.news.client.NewsClient;
import app.finup.layer.domain.news.constant.NewsRedisKey;
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
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
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
@Transactional
public class NewsSchedulerServiceImpl implements NewsSchedulerService {

    // 사용 의존성
    private final NewsRepository newsRepository;
    private final NewsClient newsClient;
    private final StockRedisStorage stockRedisStorage;

    // 병렬 로직을 제어할 의존성
    private final ExecutorService newsApiExecutor;
    private final ExecutorService crawlingExecutor;

    // 사용 상수
    private static final String QUERY_SEARCH_MAIN = "국내+주식";
    private static final Duration THRESHOLD_SAVE = Duration.ofDays(7); // 가져온 기사는 7일간 지속
    private static final int THRESHOLD_DESCRIPTION_MIN_LENGTH = 100; // 기사 본문 최소 길이
    private static final int AMOUNT_NEWS = 50; // 기사 본문 최소 길이


    @CacheEvict( // 기존 캐시 삭제
            value = NewsRedisKey.CACHE_MAIN,
            allEntries = true
    )
    @Override
    public void syncMain() {

        // [1] 오래된 Entity 삭제
        newsRepository.removeOld(LocalDateTime.now().minus(THRESHOLD_SAVE));

        // [2] 현재 뉴스 엔티티 조회
        Map<String, News> curEntityMap = // "뉴스 제목" - "News Entity" Map (뉴스 이름으로 중복 판단)
                newsRepository.findByNewsType(NewsType.MAIN)
                        .stream()
                        .collect(Collectors.toConcurrentMap(
                                News::getTitle,
                                Function.identity()
                        ));

        // [3] 뉴스 검색 수행 및 필터링 결과 기반 엔티티 생성 및 저장
        // API 뉴스 기사 및 현재 기사 목록 일괄 조회 후, 유사도 및 중복 기사 필터링
        List<NewsApi.Row> rows = newsClient.getLatest(QUERY_SEARCH_MAIN, AMOUNT_NEWS);
        List<NewsApi.Row> filteredRows = NewsFilterUtils.filter(rows);

        // [4] 필터된 결과 기반 Entity 생성 및 저장
        List<News> entities =
                filteredRows.parallelStream()
                        .filter(dto -> isNotDuplicatedTitle(dto, curEntityMap))
                        .map(this::toMainNewsEntity)
                        .filter(entity -> entity.getDescription().length() >= THRESHOLD_DESCRIPTION_MIN_LENGTH)
                        .toList();

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
        // Map<종목코드, Map<뉴스제목, News>>
        Map<String, Map<String, News>> curEntityMap = newsRepository
                .findByNewsType(NewsType.STOCK)
                .stream()
                .collect(Collectors.groupingBy(
                        News::getStockCode,  // 종목코드로 그룹화
                        Collectors.toMap(News::getTitle, Function.identity(), (existing, replacement) -> existing)
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
            request -> new StockNewsResponse(newsClient.getLatest(request.stockName, AMOUNT_NEWS), request.stockCode(), request.stockName()),
                ParallelUtils.SEMAPHORE_API_NAVER_NEWS,
                newsApiExecutor
        ).stream()
            .map(response -> Map.entry(response.stockCode, NewsFilterUtils.filter(response.stocks))) // 검색 결과 필터링
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
                        crawlingExecutor)
                .stream()
                .filter(entity -> entity.getDescription().length() >= THRESHOLD_DESCRIPTION_MIN_LENGTH)
                .toList();

        // [6] 저장
        newsRepository.saveAll(entities);
    }


    // 기존 기사와 중복되는 제목인지 판별
    private boolean isNotDuplicatedTitle(NewsApi.Row dto, Map<String, News> curEntityMap) {
        return !curEntityMap.containsKey(HtmlUtils.getText(dto.getTitle()));
    }

    // 메인 뉴스 Entity 클래스로 변환
    private News toMainNewsEntity(NewsApi.Row row) {

        return setNewsBuilder(row)
                .newsType(NewsType.MAIN)
                .build();
    }

    // 종목 뉴스 Entity 클래스로 변환
    private News toStockNewsEntity(NewsApi.Row row, String stockCode) {

        return setNewsBuilder(row)
                .newsType(NewsType.STOCK)
                .stockCode(stockCode)
                .build();
    }

    // 기본 값 설정
    private News.NewsBuilder setNewsBuilder(NewsApi.Row row) {

        // 기사 링크
        String link = row.getLink();

        return News.builder()
                .title(HtmlUtils.getText(row.getTitle()))
                .summary(HtmlUtils.getText(row.getSummary()))
                .description(NewsCrawlingUtils.extractDescription(link))
                .thumbnail(NewsCrawlingUtils.extractThumbnail(link))
                .publisher(NewsCrawlingUtils.extractPublisher(link))
                .link(link)
                .publishedAt(row.getPublishedAt());
    }

    // 종목 검색 작업 요청 DTO
    private record StockNewsRequest(String stockCode, String stockName) {}
    private record StockNewsResponse(List<NewsApi.Row> stocks, String stockCode, String stockName) {}
    private record StockNewsCrawRequest(String stockCode, NewsApi.Row row) {}
}
