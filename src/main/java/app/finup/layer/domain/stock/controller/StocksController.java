package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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

    /**
     * 종목+ 시가총액 순위 리스트 조회 API
     * [GET] /stocks
     */
    @GetMapping("/market-cap-ranking")
    public ResponseEntity<?> getMarketCapRanking(){
        //[1]요청
    return null;
    }
}
