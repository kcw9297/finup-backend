package app.finup.layer.domain.stockNews.service;

import app.finup.layer.domain.news.api.NewsApiClient;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.service.NewsRemoveDuplicateService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.util.List;

/**
 * StockNewsService 구현 클래스
 * @author oyh
 * @since 2025-12-05
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class StockNewsServiceImpl implements StockNewsService {
    private final NewsApiClient newsApiClient;
    private final NewsRemoveDuplicateService duplicateService;
    private final NewsRedisStorage newsRedisStorage;

    private static final Duration DURATION_NEWS = Duration.ofMinutes(35); // 35분
    private static final int NEWS_LIMIT = 50;

    @Override
    public List<NewsDto.Row> getNews(String category, String stockName) {
        String key = "STOCK_NEWS:"+category+":"+stockName;
        //redis 캐시 조회
        List<NewsDto.Row> cashNews = newsRedisStorage.getNews(key, new TypeReference<List<NewsDto.Row>>() {});
        if(cashNews != null) return cashNews;

        //외부 api 호출
        List<NewsDto.Row> freshNews = fetchFromExternal(category, stockName);
        List<NewsDto.Row> limitedNews = freshNews.stream().limit(NEWS_LIMIT).toList();

        //redis 캐싱
        newsRedisStorage.saveNews(key, limitedNews, DURATION_NEWS);
        log.info("[NEWS] 캐시 저장 성공 key={}", key);

        return limitedNews;
    }

    /**
     * 스케줄러에서 강제 리프레시할 때 호출
     * - 30분마다 date/sim 각각 새로 가져와 덮어쓰기
     */
    @Override
    public void refreshCategory(String category, String stockName) {
        String key = "STOCK_NEWS:"+category+":"+stockName;
        List<NewsDto.Row> freshNews = fetchFromExternal(category, stockName);
        List<NewsDto.Row> limited = freshNews.stream().limit(NEWS_LIMIT).toList();
        newsRedisStorage.saveNews(key, limited, DURATION_NEWS);
        log.info("[NEWS] 스케줄러 캐시 강제 갱신 key={}", key);
    }
    /**
     * 모든 카테고리 캐시 갱신 (스케줄러에서 1번만 호출)
     */
    @Override
    public void refreshAllCategories(String stockName) {
        refreshCategory("date", stockName);
        refreshCategory("sim", stockName);
    }

    private List<NewsDto.Row> fetchFromExternal(String category, String stockName){
        List<NewsDto.Row> parseList = newsApiClient.fetchNews(stockName, category, 50);
        List<NewsDto.Row> duplicateList = duplicateService.removeDuplicate(parseList);

        return duplicateList;
    }
}
