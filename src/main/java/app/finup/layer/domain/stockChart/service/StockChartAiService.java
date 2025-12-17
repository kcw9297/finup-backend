package app.finup.layer.domain.stockChart.service;

import app.finup.layer.domain.stockChart.dto.StockChartDto;

public interface StockChartAiService {
    /**
     * 차트 데이터(AiInput)를 받아 AI 분석 결과를 반환
     */
    StockChartDto.ChartAi analyze(String code, StockChartDto.AiInput input);
    StockChartDto.ChartAi getChartAi(String code, StockChartDto.AiInput input);
    void refreshChartAi(String code, StockChartDto.AiInput input);
}
