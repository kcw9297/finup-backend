package app.finup.layer.domain.news.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(Url.NEWS_PUBLIC)
@RestController
@RequiredArgsConstructor
public class PublicNewsController {
    private final NewsService newsService;

    @GetMapping("/latest")
    public ResponseEntity<?> getLatestNews(@RequestParam(defaultValue = "date") String category,
                                           @RequestParam(defaultValue = "10") int limit)
    {
        return Api.ok(newsService.getLatestNews(category, limit));
    }
}
