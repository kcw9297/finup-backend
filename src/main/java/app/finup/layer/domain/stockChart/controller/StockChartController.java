package app.finup.layer.domain.stockChart.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stockChart.enums.CandleType;
import app.finup.layer.domain.stockChart.service.StockChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Url.STOCKS_PUBLIC)
public class StockChartController {

    private final StockChartService stockChartService;

    @GetMapping("/chart")
    public ResponseEntity<?> getStockChart(@RequestParam String code, @RequestParam(defaultValue = "day") String candleType){
        CandleType type = CandleType.fromParam(candleType);
        return Api.ok(stockChartService.inquireDaily(code, type));
    }
}
