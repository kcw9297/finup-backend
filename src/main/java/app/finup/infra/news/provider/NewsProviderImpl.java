package app.finup.infra.news.provider;

import app.finup.layer.domain.news.api.NewsApiClient;
import app.finup.layer.domain.news.component.NewsContentExtractor;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.service.NewsAiService;
import app.finup.layer.domain.news.service.NewsRemoveDuplicateService;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsProviderImpl implements NewsProvider {
    private final NewsRedisStorage newsRedisStorage;
    private final NewsContentExtractor newsContentExtractor;
    private final NewsAiService newsAiService;
    private final NewsApiClient newsApiClient;
    private final NewsRemoveDuplicateService duplicateService;

    @Override
    public List<NewsDto.Row> getNews(String category, int limit) {
        String key = "NEWS:CATEGORY:" + category + ":" + limit;
        List<NewsDto.Row> cached = newsRedisStorage.getNews(key, new TypeReference<List<NewsDto.Row>>() {
        });
        if (cached == null) {
            log.warn("[NEWS_PROVIDER] CACHE MISS {}", key);
            return List.of(); //or fallback
        }
        log.info("[NEWS_PROVIDER] HIT {}", key);
        return cached;
    }

    private static final int MAX_DEEP = 10;
    @Override
    public List<NewsDto.Row> fetchNews(String category, int limit) {
        log.info("[NEWS_PROVIDER] Fetch Category News: {}", category);
        List<NewsDto.Row> list = newsApiClient.fetchNews("국내+주식",category,limit);
        list = duplicateService.removeDuplicate(list);
        List<NewsDto.Row> result = new ArrayList<>();
        int deepCount = 0;

        for(NewsDto.Row row : list) {
            boolean useDeep = applyAi(row, deepCount < MAX_DEEP);
            if (useDeep) deepCount++;
            result.add(row);
        }
        log.info("[NEWS_PROVIDER] DEEP={}, LIGHT={}", deepCount, result.size() - deepCount);
        return result;
    }

    @Override
    public List<NewsDto.Row> getStockNews(String keyword, String category, int limit) {
        String key = "NEWS:STOCK:" + keyword + ":" + limit;
        List<NewsDto.Row> cached = newsRedisStorage.getNews(key, new TypeReference<List<NewsDto.Row>>() {
        });
        if (cached != null) {
            log.info("[NEWS_PROVIDER] HIT {}", key);
            return cached; //or fallback
        }
        log.warn("[NEWS_PROVIDER] CACHE MISS {}", key);
        List<NewsDto.Row> fetched = fetchStockNews(keyword, category, limit);
        if(fetched == null || fetched.isEmpty()) {
            return List.of();
        }

        newsRedisStorage.saveNews(key, fetched, Duration.ofSeconds(30));
        return fetched;
    }

    @Override
    public List<NewsDto.Row> fetchStockNews(String keyword, String category, int limit) {
        log.info("[NEWS_PROVIDER] Fetch Stock News: {}", keyword);
        List<NewsDto.Row> list = newsApiClient.fetchNews(keyword, category,limit);
        list = duplicateService.removeDuplicate(list);
        return list;
    }

    private boolean applyAi(NewsDto.Row row, boolean allowDeep) {
        try{
            if(allowDeep && newsContentExtractor.isSupported(row.getLink())) {
                NewsDto.Ai deep = newsAiService.analyzeDeep(row.getLink());
                row.setSummary(null);
                row.setAi(deep);
                return true;
            }else {
                NewsDto.Summary light = newsAiService.analyzeLight(row.getDescription());
                row.setAi(null);
                row.setSummary(light);
                return false;
            }
        }catch (Exception e){
            log.warn("[NEWS_PROVIDER] AI 분석 실패, URL={}", row.getLink(), e);
            row.setAi(null);
            row.setSummary(null);
            return false;
        }
    }
}
