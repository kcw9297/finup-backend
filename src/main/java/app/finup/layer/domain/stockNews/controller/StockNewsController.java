package app.finup.layer.domain.stockNews.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stockNews.service.StockNewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Url.STOCKS_PUBLIC)
public class StockNewsController {
    private final StockNewsService stockNewsService;
    @GetMapping("/news")
    public ResponseEntity<?> getNews(String category, String stockName) {
        return Api.ok(stockNewsService.getNews(category, stockName));
    }
}
