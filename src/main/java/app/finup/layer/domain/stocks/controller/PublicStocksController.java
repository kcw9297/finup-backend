package app.finup.layer.domain.stocks.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stocks.service.StocksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(Url.NEWS_PUBLIC)
@RestController
@RequiredArgsConstructor
public class PublicStocksController {

    //private final StocksService stocksService;
    /*
    @GetMapping("/market-cap-ranking")
    public ResponseEntity<?> getMarketCapRanking() {
        return Api.ok(stocksService.getMarketCapRanking());

    }*/

    //@GetMapping("/stocks/{idx}")
    //public ResponseEntity<?> getDetail() {
    //    return Api.ok(stocksService.getDetail());
    //}
}
