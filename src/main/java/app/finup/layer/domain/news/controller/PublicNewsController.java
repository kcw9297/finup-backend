package app.finup.layer.domain.news.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.service.NewsAiService;
import app.finup.layer.domain.news.service.NewsService;
import com.fasterxml.jackson.core.JsonProcessingException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@Slf4j
@RequestMapping(Url.NEWS_PUBLIC)
@RestController
@RequiredArgsConstructor
public class PublicNewsController {

    private final NewsService newsService;
    private final NewsAiService newsAiService;

    @GetMapping("/list")
    public ResponseEntity<?> getNews(int page, String keyword, String category) {
        return Api.ok(newsService.getNews(page, keyword, category));

    }

    @GetMapping("/detail-ai")
    public ResponseEntity<?> getArticleByAi(@RequestParam String url) throws JsonProcessingException {
        String article = newsService.extractArticle(url);
        Map<String,Object> ai = newsAiService.analyzeArticle(article);

        return Api.ok(Map.of("content",article,"ai",ai));
    }

}
