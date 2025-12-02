package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.util.NewsScraper;
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
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

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

    @Override
    public List<NewsDto.Summary> getNews(int page, String keyword, String category) {
        keyword = "국내주식";

        String json = fetchNewsJson(page, keyword, category);
        if(json == null) return List.of();
        List<NewsDto.Summary> list = parseNewsJson(json);
        //언론사 필터링
        list = filterAllowedPress(list);
        //url 기준 중복 삭제
        list = distinctByUrl(list);

        return list;
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

    private String fetchNewsJson(int page, String keyword, String category) {
        int display = 20;
        int start = (page-1) * display + 1;

        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("query", keyword)
                        .queryParam("display", display)
                        .queryParam("sort", category)
                        .queryParam("start", start)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }
    private List<NewsDto.Summary> parseNewsJson(String json) {
        List<NewsDto.Summary> result = new ArrayList<>();

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

                NewsDto.Summary dto = NewsDtoMapper.toSummary(
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
    private List<NewsDto.Summary> filterAllowedPress(List<NewsDto.Summary> list) {
        return list.stream()
                .filter(item -> NewsDto.ALLOWED_PRESS.contains(item.getPublisher()))
                .toList();
    }

    private List<NewsDto.Summary> distinctByUrl(List<NewsDto.Summary> list) {
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
