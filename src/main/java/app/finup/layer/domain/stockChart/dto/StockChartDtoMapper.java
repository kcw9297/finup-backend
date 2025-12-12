package app.finup.layer.domain.stockChart.dto;

import java.util.List;
import java.util.Map;

public class StockChartDtoMapper {
    public static StockChartDto.CandleItem toCandleItem(StockChartDto.Detail d) {
        return new StockChartDto.CandleItem(
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
    }
    public static StockChartDto.AiInput toAiInput(String timeframe, List<StockChartDto.Detail> details) {
        List<StockChartDto.CandleItem> candles = details.stream()
                .map(StockChartDtoMapper::toCandleItem)
                .toList();

        return new StockChartDto.AiInput(timeframe, candles);
    }

    public static StockChartDto.ChartAi toChartAi(Map<String, Object> result, String timeframe) {
        return new StockChartDto.ChartAi(
                (String) result.getOrDefault("trend", "N/A"),
                (String) result.getOrDefault("volatility", "N/A"),
                (String) result.getOrDefault("volumeAnalysis", "N/A"),
                (String) result.getOrDefault("summary", "N/A"),
                timeframe
        );
    }

    private static double parseDouble(String v) {
        try { return Double.parseDouble(v); }
        catch (Exception e) { return 0.0; }
    }

    private static long parseLong(String v) {
        try { return Long.parseLong(v); }
        catch (Exception e) { return 0L; }
    }
}
