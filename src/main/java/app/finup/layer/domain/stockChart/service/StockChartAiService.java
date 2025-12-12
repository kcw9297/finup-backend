package app.finup.layer.domain.stockChart.service;

import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.enums.CandleType;

import java.util.List;

public interface StockChartAiService {

    StockChartDto.ChartAi analyze(String symbol, CandleType candleType);
}
