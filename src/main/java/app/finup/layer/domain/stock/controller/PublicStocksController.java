package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.service.StockAiService;
import app.finup.layer.domain.stock.service.StockService;
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

    private final StockService stockService;
    private final StockAiService stockAiService;

    /**
     * 종목 리스트 페이지 시가총액 조회 API
     * [GET] stocks/market-cap-ranking
     */
    @GetMapping("/market-cap-ranking")
    public ResponseEntity<?> getMarketCapRanking() {
        return Api.ok(stockService.getMarketCapRow());
    }

    /**
     * 종목 리스트 페이지 거래대금 조회 API
     * [GET] stocks/trading-value-ranking
     */
    @GetMapping("/trading-value-ranking")
    public ResponseEntity<?> getTradingValueRanking() {
        return Api.ok(stockService.getTradingValueRow());
    }

    /**
     * 종목명 종목코드 kis 마스터 파일 읽어 DB 저장
     * [GET] stocks/import/stockName
     */
    @GetMapping("/import/stock-name")
    public ResponseEntity<?> importStockName() throws Exception {
        stockService.importStockName();
        return Api.ok("kospi kosdaq 파일 읽어 DB 저장");
    }

    /**
     * 종목 상세페이지 조회 API
     * [GET] stocks/detail/{code}
     * @param code 종목코드 // 문자열도 있음
     */
    @GetMapping("/detail/{code}")
    public ResponseEntity<?> getDetail(@PathVariable String code) {
        return Api.ok(stockService.getDetail(code));
    }

    /**
     * 종목 뉴스 리스트  조회 API
     * [GET] stocks/news?category={category}&stockName={stockName}
     * @param  category  // 문자열도 있음
     */
    @GetMapping("/news")
    public ResponseEntity<?> getNews(String category, String stockName) {
        return Api.ok(stockService.getStockNews(stockName, category));
    }

    /**
     * 종목 상세페이지 조회 API
     * [GET] stocks/detail/stock-ai/{code}
     * @param code 종목코드 // 문자열도 있음
     */
    @GetMapping("/detail/stock-ai/{code}")
    public ResponseEntity<?> getStockAi(@PathVariable String code) {
        StockDto.Detail detail = (stockService.getDetail(code));
        return Api.ok(stockAiService.getStockAi(code, detail));
    }
}
