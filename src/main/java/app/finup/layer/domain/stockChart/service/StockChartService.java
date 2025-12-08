package app.finup.layer.domain.stockChart.service;

import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.enums.CandleType;

public interface StockChartService {
    StockChartDto.Row inquireDaily(String code, CandleType candleType);
}