package app.finup.layer.domain.stockChart.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.dto.StockChartDtoMapper;
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
@RequestMapping(Url.STOCKS)
public class StockChartController {

    private final StockChartService stockChartService;
    private final StockChartAiService stockChartAiService;

    @GetMapping("/chart")
    public ResponseEntity<?> getStockChart(@RequestParam String code,
                                           @RequestParam(defaultValue = "day") String candleType){
        CandleType type = CandleType.fromParam(candleType);
        return Api.ok(stockChartService.getInquireDaily(code, type));
    }

    @GetMapping("/chart/ai")
    public ResponseEntity<?> analyzeChart(
            @RequestParam String code,
            @RequestParam(defaultValue = "day") String candleType
    ) {
        CandleType type = CandleType.fromParam(candleType);

        // 여기서도 KIS API 직접 안 부르고, 항상 이 서비스만 호출
        StockChartDto.Row chart = stockChartService.getInquireDaily(code, type);

        StockChartDto.AiInput input = StockChartDtoMapper.toAiInput(
                type.name(),          // DAY / WEEK / MONTH
                chart.getOutput()
        );

        StockChartDto.ChartAi ai = stockChartAiService.getChartAi(code, type, input);

        return Api.ok(ai);
    }


}
