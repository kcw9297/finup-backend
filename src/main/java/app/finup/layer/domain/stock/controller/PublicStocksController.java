package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
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
    /*
    @GetMapping("/market-cap-ranking")
    public ResponseEntity<?> getMarketCapRanking() {
        return Api.ok(stocksService.getMarketCapRanking());
    }*/
    /**
     * 종목명 종목코드 kis 마스터 파일 읽어 DB 저장
     * [GET] stocks/detail/{code}
     */
    @GetMapping("/import/kospi")
    public ResponseEntity<?> importKospi() throws Exception {
        stockService.importKospi();
        return Api.ok();
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
}
