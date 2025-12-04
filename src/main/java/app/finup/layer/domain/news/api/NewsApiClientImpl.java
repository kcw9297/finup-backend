package app.finup.layer.domain.news.api;

import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.util.NewsScraper;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;

import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
/**
 * NewsApiClient 구현 클래스
 * @author oyh
 * @since 2025-12-04
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsApiClientImpl implements NewsApiClient {
    @Value("${api.naver-news.client.id}")
    private String clientId;

    @Value("${api.naver-news.client.secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;
    private final NewsScraper newsScraper;

    @Override
    public List<NewsDto.Row> fetchNews(String query,String sort, int display) {

        String json = callNaverApi(query,sort,display);
        return parseNaverJson(json);
    }
    //webclient 호출 담당
    private String callNaverApi(String query,String sort, int display){
        WebClient client = WebClient.builder()
                .baseUrl("https://openapi.naver.com")
                .defaultHeader("X-Naver-Client-Id", clientId)
                .defaultHeader("X-Naver-Client-Secret", clientSecret)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();

        return client.get()
                .uri(uriBuilder -> uriBuilder
                        .path("/v1/search/news.json")
                        .queryParam("query", query)
                        .queryParam("display", display)
                        .queryParam("sort", sort)
                        .build()
                )
                .retrieve()
                .bodyToMono(String.class)
                .block();
    }

    //네이버가 제공한 내용 json으로 파싱
    private List<NewsDto.Row> parseNaverJson(String json){
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
}
