package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.api.NewsApiClient;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.service.NewsRemoveDuplicateService;
import app.finup.layer.domain.news.service.StockNewsAiService;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.service.StockAiService;
import app.finup.layer.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 종목+ 리스트 REST API 클래스
 * @author lky
 * @since 2025-12-01
 */

@RestController
@RequestMapping(Url.STOCKS)
@RequiredArgsConstructor
public class StocksController {
    //private final StocksService stocksService;
    private final StockService stockService;
    private final StockAiService stockAiService;
    private final StockNewsAiService stockNewsAiService;
    private final NewsApiClient newsApiClient;
    private final NewsRemoveDuplicateService newsRemoveDuplicateService;

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
        try {
            return Api.ok(stockService.getDetail(code)); // 정상 데이터
        } catch (RuntimeException e) {
            return Api.fail(
                    "종목 조회 중 오류가 발생했습니다.",
                    "SERVER_ERROR",
                    500
            );
        }
    }

    /**
     * 종목 뉴스 리스트  조회 API
     * [GET] stocks/news?stockName={stockName}
     * @param  stockName
     */
    @GetMapping("/news")
    public ResponseEntity<?> getStockNews(String stockName) {
        return Api.ok(stockService.getStockNews(stockName));
    }

    @PostMapping("/news/ai")
    public ResponseEntity<?> getStockNewsAi(@RequestBody NewsDto.AiRequest req ) {
        return Api.ok(stockNewsAiService.analyzeAuto(req.getLink(), req.getDescription()));
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
