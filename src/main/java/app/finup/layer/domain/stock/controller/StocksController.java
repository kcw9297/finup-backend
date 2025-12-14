package app.finup.layer.domain.stock.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.service.StockNewsAiService;
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
    private final StockNewsAiService stockNewsAiService;

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
