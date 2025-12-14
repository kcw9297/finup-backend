package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.stock.dto.StockDto;

import java.util.Map;

public interface StockAiService {
    Map<String, Object> getStockAi(StockDto.Detail detail);

    //StockDto.YoutubeSearchResponse getYoutubeVideo(String keyword);
    //void refreshYoutubeVideo(String keyword);
}
