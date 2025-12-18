package app.finup.layer.domain.stockChart.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.stock.redis.StockStorage;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.dto.StockChartDtoMapper;
import app.finup.layer.domain.stockChart.enums.CandleType;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;

import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class StockChartAiServiceImpl implements StockChartAiService {
    private final AiManager aiManager;
    @Qualifier("objectMapper")
    private final ObjectMapper objectMapper;
    private final StockStorage stockStorage;

    @Override
    public StockChartDto.ChartAi analyze(String code, StockChartDto.AiInput input) {
        // 방어 로직: 데이터 없으면 바로 실패 응답
        if (input == null || input.getCandles() == null || input.getCandles().isEmpty()) {
            String tf = (input != null) ? input.getTimeframe() : null;
            return new StockChartDto.ChartAi(
                    "분석할 데이터가 없습니다.",
                    "분석할 데이터가 없습니다.",
                    "분석할 데이터가 없습니다.",
                    "분석할 데이터가 없습니다.",
                    tf
            );
        }
        try {
            // 1) AiInput → JSON 문자열
            String candlesJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(input);

            // 2) 프롬프트 구성 (형이 만든 CHART_ANALYSIS 템플릿 기준)
            String prompt = PromptTemplates.CHART_ANALYSIS
                    .replace("{TIMEFRAME}", input.getTimeframe())
                    .replace("{COUNT}", String.valueOf(input.getCandles().size()))
                    .replace("{CANDLES_JSON}", candlesJson);

            // 3) GPT 실행 (JSON 응답 받기)
            Map<String, Object> result = aiManager.runJsonPrompt(prompt);

            // 4) Map → ChartAi DTO 변환
            return StockChartDtoMapper.toChartAi(result, input.getTimeframe());

        } catch (Exception e) {
            log.error("차트 AI 분석 실패", e);

            return new StockChartDto.ChartAi(
                    "분석 중 오류가 발생했습니다.",
                    "분석 중 오류가 발생했습니다.",
                    "분석 중 오류가 발생했습니다.",
                    "일시적인 오류로 분석 결과를 가져오지 못했습니다.",
                    input.getTimeframe()
            );
        }
    }

    @Override
    public StockChartDto.ChartAi getChartAi(String code, CandleType candleType, StockChartDto.AiInput input) {
        StockChartDto.ChartAi chartAi = stockStorage.getChartAi(code, candleType);
        if (chartAi == null) {
            refreshChartAi(code, candleType, input);
            chartAi = stockStorage.getChartAi(code, candleType);
        }else{
            log.info("종목 차트 AI분석 Redis에서 가져옴");
        }
        if(chartAi==null) return null;
        return chartAi;
    }

    @Override
    public void refreshChartAi(String code, CandleType candleType, StockChartDto.AiInput input) {
        try {
            // 1) AiInput → JSON 문자열
            String candlesJson = objectMapper
                    .writerWithDefaultPrettyPrinter()
                    .writeValueAsString(input);

            // 2) 프롬프트 구성
            String prompt = PromptTemplates.CHART_ANALYSIS
                    .replace("{TIMEFRAME}", input.getTimeframe())
                    .replace("{COUNT}", String.valueOf(input.getCandles().size()))
                    .replace("{CANDLES_JSON}", candlesJson);

            // 3) GPT 실행 (JSON 응답 받기)
            Map<String, Object> result = aiManager.runJsonPrompt(prompt);

            // 4) Map → ChartAi DTO 변환
            StockChartDto.ChartAi chartAi = StockChartDtoMapper.toChartAi(result, input.getTimeframe());
            log.info("종목 차트 AI분석 갱신함 code={}", code);
            stockStorage.setChartAi(code, candleType, chartAi);

        } catch (Exception e) {
            log.error("차트 AI 분석 실패", e);
        }
    }
}
