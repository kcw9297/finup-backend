package app.finup.layer.domain.news.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * News REST API 클래스
 * @author oyh
 * @since 2025-12-01
 */
@Slf4j
@RequestMapping(Url.NEWS)
@RestController
@RequiredArgsConstructor
public class NewsController {

    private final NewsService newsService;

    @GetMapping("/list")
    public ResponseEntity<?> getNews(String category) {
        return Api.ok(newsService.getNews(category));

    }
}
