package app.finup.layer.domain.stockChart.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.dto.StockChartDtoMapper;
import app.finup.layer.domain.stockChart.enums.CandleType;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Slf4j
@RequiredArgsConstructor
@Service
public class StockChartAiServiceImpl implements StockChartAiService {

    private final AiManager aiManager;
    private final ObjectMapper objectMapper;
    private final StockChartService stockChartService;

    @Override
    public StockChartDto.ChartAi analyze(String code, CandleType candleType) {
        StockChartDto.Row row = stockChartService.inquireDaily(code, candleType);
        List<StockChartDto.Detail> details = row.getOutput();

        if (details == null || details.size() < 30) {
            // 데이터 부족하면 null, 예외, 또는 기본 메시지 등 형 스타일대로
            return null;
        }
        //Detail → CandleAi 변환
        List<StockChartDto.CandleAi> candles =
                StockChartDtoMapper.toAi(details);

        //캔들 json 직렬화
        String candlesJson;
        try{
           candlesJson = objectMapper.writeValueAsString(candles);
        }catch (JsonProcessingException e){
            throw new IllegalArgumentException("차트 캔들 json 변환 실패", e);
        }
        //프롬프터 파라미터 치환
        String prompt = PromptTemplates.CHART_ANALYSIS
                .replace("{SYMBOL}", code)
                .replace("{TIMEFRAME}", candleType.name())
                .replace("{COUNT}", String.valueOf(candles.size()))
                .replace("{CANDLES_JSON}", candlesJson);
        //ai호출
        Map<String, Object> result= aiManager.runJsonPrompt(prompt);
        if(result==null || result.isEmpty()){
            throw new IllegalStateException("차트 AI분석 결과가 비어있습니다.");
        }
        return toChartAi(result, candleType);
    }
    private StockChartDto.ChartAi toChartAi(Map<String, Object> result, CandleType candleType) {
        StockChartDto.ChartAi ai = new StockChartDto.ChartAi();
        ai.setTrend((String) result.get("trend"));
        ai.setVolatility((String) result.get("volatility"));
        ai.setVolumeAnalysis((String) result.get("volumeAnalysis"));
        ai.setSummary((String) result.get("summary"));
        ai.setTimeframe(candleType.name());

        return ai;
    }
}
