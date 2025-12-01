package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.dto.NewsDtoMapper;
import app.finup.layer.domain.news.util.NewsScraper;
import com.fasterxml.jackson.core.JsonProcessingException;
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

import java.time.LocalDateTime;
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

    @Value("${naver.client.id}")
    private String clientId;

    @Value("${naver.client.secret}")
    private String clientSecret;

    private final ObjectMapper objectMapper;
    private final NewsScraper newsScraper;

    @Override
    public List<NewsDto.Summary> getNews(int page, String keyword) {
        String responseJson = fetchNewsJson(page, keyword);
        if (responseJson == null) return List.of();

        List<NewsDto.Summary> list = parseNewsJson(responseJson);
        list = filterAllowedPress(list);
        list = distinctByUrl(list);

        return list;
    }
    private String fetchNewsJson(int page, String keyword) {
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
                        .queryParam("sort", "date")
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




}
