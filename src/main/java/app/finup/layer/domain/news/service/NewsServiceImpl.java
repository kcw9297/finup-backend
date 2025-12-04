package app.finup.layer.domain.news.service;

import app.finup.infra.ai.AiManager;
import app.finup.layer.domain.news.api.NewsApiClient;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.util.NewsScraper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.Duration;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
/**
 * NewsService 구현 클래스
 * @author oyh
 * @since 2025-12-01
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {

    private final NewsApiClient newsApiClient;
    private final NewsRedisStorage newsRedisStorage;
    private static final Duration DURATION_NEWS = Duration.ofMinutes(30); // 30분
    private static final int NEWS_LIMIT = 100;
    /**
     * 프론트에서 호출하는 메인 메서드
     * GET /news/list?category=date
     */
    @Override
    public List<NewsDto.Row> getNews(String category) {
        String key = "NEWS:"+category;

        //redis 캐시 조회
        List<NewsDto.Row> cashNews = newsRedisStorage.getNews(key, new TypeReference<List<NewsDto.Row>>() {});
        if(cashNews != null) return cashNews;

        //외부 api 호출
        List<NewsDto.Row> freshNews = fetchFromExternal(category);
        List<NewsDto.Row> limitedNews = freshNews.stream().limit(NEWS_LIMIT).toList();

        //redis 캐싱
        newsRedisStorage.saveNews(key, limitedNews, DURATION_NEWS);
        log.info("[NEWS] 캐시 저장 성공 key={}", key);

        return freshNews;
    }

    /**
     * 스케줄러에서 강제 리프레시할 때 호출
     * - 30분마다 date/sim 각각 새로 가져와 덮어쓰기
     */
    @Override
    public void refreshCategory(String category) {
        String key = "NEWS:"+category;
        List<NewsDto.Row> freshNews = fetchFromExternal(category);
        List<NewsDto.Row> limited = freshNews.stream().limit(NEWS_LIMIT).toList();
        newsRedisStorage.saveNews(key, limited, DURATION_NEWS);
        log.info("[NEWS] 스케줄러 캐시 강제 갱신 category={}, key={}", category, key);
    }
    /**
     * 모든 카테고리 캐시 갱신 (스케줄러에서 1번만 호출)
     */
    @Override
    public void refreshAllCategories() {
        refreshCategory("date");
        refreshCategory("sim");
    }

    private List<NewsDto.Row> fetchFromExternal(String category) {
        log.info("[NEWS] 외부 뉴스 API 호출 category={}, limit={}", category, NEWS_LIMIT);
        List<NewsDto.Row> parseList = newsApiClient.fetchNews(category);
        List<NewsDto.Row> distinct = distinctByUrl(parseList);

        return distinct;
    }

    private List<NewsDto.Row> distinctByUrl(List<NewsDto.Row> list) {
        Set<String> seen = new HashSet<>();
        return list.stream()
                .filter(item -> seen.add(item.getLink()))
                .toList();
    }

}
