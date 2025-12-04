package app.finup.layer.domain.stocks.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stocks.service.StocksService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 종목+ 리스트 REST API 클래스
 * @author lky
 * @since 2025-12-03
 */

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

    /**
     * 종목 상세페이지 조회 API
     * [GET] stocks/detail/{code}
     * @param code 종목코드
     */
    @GetMapping("/detail/{code:[0-9]+}")
    public ResponseEntity<?> getDetail(@PathVariable String code) {
        return Api.ok(stocksService.getDetail(code));
    }
}
