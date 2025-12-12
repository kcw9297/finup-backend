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
@RequestMapping(Url.STOCKS_PUBLIC)
public class StockChartController {

    private final StockChartService stockChartService;
    private final StockChartAiService stockChartAiService;

    @GetMapping("/chart")
    public ResponseEntity<?> getStockChart(@RequestParam String code,
                                           @RequestParam(defaultValue = "day") String candleType){
        CandleType type = CandleType.fromParam(candleType);
        return Api.ok(stockChartService.inquireDaily(code, type));
    }

    @GetMapping("/chart/ai")
    public ResponseEntity<?> analyzeChart(
            @RequestParam String code,
            @RequestParam(defaultValue = "day") String candleType
    ) {
        CandleType type = CandleType.fromParam(candleType);

        // 여기서도 KIS API 직접 안 부르고, 항상 이 서비스만 호출
        StockChartDto.Row chart = stockChartService.inquireDaily(code, type);

        StockChartDto.AiInput input = StockChartDtoMapper.toAiInput(
                type.name(),          // DAY / WEEK / MONTH
                chart.getOutput()
        );

        StockChartDto.ChartAi ai = stockChartAiService.analyze(input);

        return Api.ok(ai);
    }
//    @GetMapping("/chart")
//    public ResponseEntity<?> getStockChart(
//            @RequestParam String code,
//            @RequestParam(defaultValue = "day") String candleType,
//            @RequestParam(defaultValue = "false") boolean withAi
//    ) {
//        // 1) candleType 변환
//        CandleType type = CandleType.fromParam(candleType);
//
//        // 2) KIS에서 차트 한 번만 조회
//        StockChartDto.Row chart = stockChartService.inquireDaily(code, type);
//
//        // 2-1) AI 안 쓰면 기존처럼 차트만 반환
//        if (!withAi) {
//            return Api.ok(chart);
//        }
//
//        // 3) Detail → AiInput 변환
//        StockChartDto.AiInput input = StockChartDtoMapper.toAiInput(
//                type.name(),           // DAY / WEEK / MONTH
//                chart.getOutput()      // List<Detail>
//        );
//
//        // 4) AI 분석 실행
//        StockChartDto.ChartAi ai = stockChartAiService.analyze(input);
//
//        // 5) 차트 + AI 합친 응답 DTO
//        StockChartDto.ChartWithAi response = StockChartDtoMapper.toChartWithAi(chart, ai);
//
//        return Api.ok(response);
//    }


}
