package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 주식 종목정보 제공 REST API 공용 클래스
 * @author kcw
 * @since 2026-01-13
 */
@Slf4j
@RestController
@RequestMapping(Url.STOCKS_PUBLIC)
@RequiredArgsConstructor
public class PublicStocksController {

    private final StockService stockService;

    /**
     * 종목 리스트 페이지 시가총액 조회 API
     * [GET] /stocks/market-cap
     */
    @GetMapping("/market-cap")
    public ResponseEntity<?> getMarketCapList() {
        return Api.ok(stockService.getMarketCapList());
    }

    /**
     * 종목 리스트 페이지 거래대금 조회 API
     * [GET] /stocks/trading-value
     */
    @GetMapping("/trading-value")
    public ResponseEntity<?> getTradingValueRanking() {
        return Api.ok(stockService.getTradingValueList());
    }


}
