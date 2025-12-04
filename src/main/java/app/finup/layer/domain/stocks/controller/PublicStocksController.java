package app.finup.layer.domain.stocks.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stocks.service.StocksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(Url.STOCKS_PUBLIC)
@RestController
@RequiredArgsConstructor
public class PublicStocksController {

    private final StocksService stocksService;
    /*
    @GetMapping("/market-cap-ranking")
    public ResponseEntity<?> getMarketCapRanking() {
        return Api.ok(stocksService.getMarketCapRanking());
    }*/

    @GetMapping("/detail/{code:[0-9]+}")
    public ResponseEntity<?> getDetail(@PathVariable String code) {
        return Api.ok(stocksService.getDetail(code));
    }
}
