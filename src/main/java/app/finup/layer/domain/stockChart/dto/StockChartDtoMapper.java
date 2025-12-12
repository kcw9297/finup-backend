package app.finup.layer.domain.stockChart.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockChartDtoMapper {

    /**
     * KIS 차트 Detail 리스트 → AI 입력용 AiInput 으로 변환
     * - timeframe : DAY / WEEK / MONTH (컨트롤러/서비스에서 넘겨줌)
     * - candles   : 최근 30개를 날짜 오름차순으로 정렬해서 사용
     */
    public static StockChartDto.AiInput toAiInput(String timeframe, List<StockChartDto.Detail> details) {
        if (details == null || details.isEmpty()) {
            return new StockChartDto.AiInput(timeframe, List.of());
        }

        // 1) 복사본 만들어서 날짜 기준 정렬 (오름차순: 예전 → 최근)
        List<StockChartDto.Detail> sorted = new ArrayList<>(details);
        sorted.sort(Comparator.comparing(StockChartDto.Detail::getStck_bsop_date));

        // 2) 최근 30개만 사용 (30개 미만이면 전체)
        if (sorted.size() > 30) {
            sorted = sorted.subList(sorted.size() - 30, sorted.size());
        }

        // 3) CandleItem 리스트로 변환
        List<StockChartDto.CandleItem> candles = new ArrayList<>();
        for (StockChartDto.Detail d : sorted) {
            StockChartDto.CandleItem item = new StockChartDto.CandleItem(
                    d.getStck_bsop_date(),
                    parseDouble(d.getStck_oprc()),
                    parseDouble(d.getStck_hgpr()),
                    parseDouble(d.getStck_lwpr()),
                    parseDouble(d.getStck_clpr()),
                    parseLong(d.getAcml_vol()),
                    d.getMa5(),
                    d.getMa20(),
                    d.getMa60(),
                    d.getVolumeMa5(),
                    d.getVolumeMa20()
            );
            candles.add(item);
        }

        return new StockChartDto.AiInput(timeframe, candles);
    }

    /**
     * AI JSON 응답(Map) → ChartAi DTO 변환
     */
    public static StockChartDto.ChartAi toChartAi(Map<String, Object> result, String timeframe) {
        if (result == null) {
            return new StockChartDto.ChartAi(
                    "분석 결과 없음",
                    "분석 결과 없음",
                    "분석 결과 없음",
                    "분석 결과 없음",
                    timeframe
            );
        }

        String trend = safeString(result.get("trend"), "N/A");
        String volatility = safeString(result.get("volatility"), "N/A");
        String volumeAnalysis = safeString(result.get("volumeAnalysis"), "N/A");
        String summary = safeString(result.get("summary"), "N/A");

        // 프롬프트에서 timeframe도 내려주긴 하지만, 우선순위는 메서드 인자
        String tf = timeframe != null ? timeframe : safeString(result.get("timeframe"), "N/A");

        return new StockChartDto.ChartAi(
                trend,
                volatility,
                volumeAnalysis,
                summary,
                tf
        );
    }

    /**
     * 차트 + AI를 한 번에 응답으로 내려줄 DTO 생성
     */
    public static StockChartDto.ChartWithAi toChartWithAi(StockChartDto.Row chart, StockChartDto.ChartAi ai) {
        return new StockChartDto.ChartWithAi(chart, ai);
    }

    private static Double parseDouble(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Double.parseDouble(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static Long parseLong(String value) {
        if (value == null || value.isBlank()) return null;
        try {
            return Long.parseLong(value.trim());
        } catch (NumberFormatException e) {
            return null;
        }
    }

    private static String safeString(Object value, String defaultValue) {
        return value == null ? defaultValue : String.valueOf(value);
    }
}
