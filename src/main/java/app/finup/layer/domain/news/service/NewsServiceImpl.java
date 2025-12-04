package app.finup.layer.domain.news.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.infra.redis.manager.RedisCacheManager;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.util.NewsScraper;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.reactive.function.client.WebClient;

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

    @Value("${api.naver-news.client.id}")
    private String clientId;

    @Value("${api.naver-news.client.secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;
    private final NewsScraper newsScraper;
    private final AiManager aiManager;
    private final RedisCacheManager  redisCacheManager;

    private static final long NEWS_TTL_MILLIS = 30 * 60 * 1000L; // 30분
    private static final int NEWS_LIMIT = 100;
    /**
     * 프론트에서 호출하는 메인 메서드
     * GET /news/list?category=date
     */
    @Override
    public List<NewsDto.Row> getNews(String category) {
        String key = "NEWS:"+category;
        try{
            String cachedJson = redisCacheManager.getNews(key);
            if(cachedJson != null){
                log.debug("[NEWS] 캐시 HIT key={}", key);
                return objectMapper.readValue(cachedJson, new TypeReference<List<NewsDto.Row>>() {});
            }
        }catch (Exception e){
            log.warn("[NEWS] 캐시 조회/파싱 실패 → 외부 API로 fallback. key={}, err={}", key, e.getMessage());
        }

        List<NewsDto.Row> freshNews = fetchFromExternal(category);

        try {
            String json = objectMapper.writeValueAsString(freshNews);
            redisCacheManager.saveNews(key, json, NEWS_TTL_MILLIS);
            log.debug("[NEWS] 캐시 저장 성공 key={}", key);
        } catch (Exception e) {
            log.warn("[NEWS] 캐시 저장 실패 key={}, err={}", key, e.getMessage());
        }
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

        try{
            String json = objectMapper.writeValueAsString(freshNews);
            redisCacheManager.saveNews(key, json, NEWS_TTL_MILLIS);
            log.info("[NEWS] 캐시 강제 갱신 완료 key={}", key);
        } catch (Exception e) {
            log.error("[NEWS] 캐시 강제 갱신 실패 key={}, err={}", key, e.getMessage());
        }
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
        String json = fetchNewsJson(category);
        List<NewsDto.Row> parseList = parseNewsJson(json);
        List<NewsDto.Row> filteredPress = filterAllowedPress(parseList);
        List<NewsDto.Row> distinct = distinctByUrl(filteredPress);

        return distinct.stream()
                .limit(NEWS_LIMIT)
                .toList();
    }
    @Override
    public Map<String, Object> analyzeNews(String article) {
        String prompt = PromptTemplates.NEWS_ANALYSIS.replace("{ARTICLE}", article);
        return aiManager.runJsonPrompt(prompt);
    }


    @Override
    public String extractArticle(String url) {
        try {
            // User-Agent 지정 중요 (언론사 일부 bot 차단 있음)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(5000)
                    .get();

            // 언론사별 본문 CSS 선택자 시도
            String content = tryExtractContent(doc);

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 오류 시 빈 문자열
        }
    }

    private String fetchNewsJson(String category) {
        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("query", "국내 주식")
                        .queryParam("display", 100)
                        .queryParam("sort", category)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    private List<NewsDto.Row> parseNewsJson(String json) {
        List<NewsDto.Row> result = new ArrayList<>();

        try {
            JsonNode root = objectMapper.readTree(json);
            JsonNode items = root.path("items");

            for (JsonNode item : items) {
                String title = Jsoup.parse(item.path("title").asText()).text();
                String summary = Jsoup.parse(item.path("description").asText()).text();
                String link = item.path("link").asText();

                String thumbnail = newsScraper.extractThumbnail(link);
                String publisher = newsScraper.extractPublisher(link);

                ZonedDateTime zdt = ZonedDateTime.parse(
                        item.path("pubDate").asText(),
                        DateTimeFormatter.RFC_1123_DATE_TIME
                );

                NewsDto.Row dto = NewsDtoMapper.toRow(
                        title,
                        summary,
                        thumbnail,
                        publisher,
                        zdt.toLocalDateTime(),
                        link
                );

                result.add(dto);
            }

        } catch (Exception e) {
            log.error("뉴스 파싱 에러: {}", e.getMessage());
        }

        return result;
    }
    private List<NewsDto.Row> filterAllowedPress(List<NewsDto.Row> list) {
        return list.stream()
                .filter(item -> NewsDto.ALLOWED_PRESS.contains(item.getPublisher()))
                .toList();
    }

    private List<NewsDto.Row> distinctByUrl(List<NewsDto.Row> list) {
        Set<String> seen = new HashSet<>();
        return list.stream()
                .filter(item -> seen.add(item.getLink()))
                .toList();
    }

    private String tryExtractContent(Document doc) {

        // 연합뉴스
        Element yna = doc.selectFirst("article#articleWrap");
        if (yna != null) return yna.text();

        // 한국경제
        Element hk = doc.selectFirst("div#articletxt, div.article-body");
        if (hk != null) return hk.text();

        // 조선비즈
        Element cbiz = doc.selectFirst("div#news_body_id, div.article-body");
        if (cbiz != null) return cbiz.text();

        // 매일경제
        Element mk = doc.selectFirst("div#article_body, div#article_body_id, section.article");
        if (mk != null) return mk.text();

        // 머니투데이
        Element mt = doc.selectFirst("div#article, div#textBody");
        if (mt != null) return mt.text();

        // 아시아경제
        Element asiae = doc.selectFirst("div#articleBody, div#txt_content");
        if (asiae != null) return asiae.text();

        // 뉴시스
        Element newsis = doc.selectFirst("div#content, div.viewBox");
        if (newsis != null) return newsis.text();

        // 파이낸셜뉴스
        Element fn = doc.selectFirst("div#article_content, div#article_body");
        if (fn != null) return fn.text();

        // 디지털타임스
        Element dt = doc.selectFirst("div#articleBody, div.article_txt");
        if (dt != null) return dt.text();

        // 전자신문
        Element et = doc.selectFirst("div#articleBody, div#articleTxt");
        if (et != null) return et.text();

        // 헤럴드경제
        Element herald = doc.selectFirst("div#articleText, div.article-text");
        if (herald != null) return herald.text();

        // 그 외 기본적인 기사 body 후보들
        Element generic = doc.selectFirst("article, div.article, div#content, div.story-body, section");
        if (generic != null) return generic.text();

        return "";
    }




}
