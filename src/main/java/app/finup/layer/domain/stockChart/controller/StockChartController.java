package app.finup.layer.domain.stockChart.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.enums.CandleType;
import app.finup.layer.domain.stockChart.service.StockChartAiService;
import app.finup.layer.domain.stockChart.service.StockChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequiredArgsConstructor
@RestController
@RequestMapping(Url.STOCKS_PUBLIC)
public class StockChartController {

    private final StockChartService stockChartService;
    private final StockChartAiService stockChartAiService;

    @GetMapping("/chart")
    public ResponseEntity<?> getStockChart(@RequestParam String code, @RequestParam(defaultValue = "day") String candleType){
        CandleType type = CandleType.fromParam(candleType);
        return Api.ok(stockChartService.inquireDaily(code, type));
    }

    @GetMapping("/chart/ai-analysis")
    public ResponseEntity<?> analyzeChart(@RequestParam(name = "code") String code, @RequestParam(name = "candleType") CandleType candleType) {
        return Api.ok(stockChartAiService.analyze(code, candleType));
    }

}
