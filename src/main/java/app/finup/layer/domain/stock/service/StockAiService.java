package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.stock.dto.StockDto;

import java.util.List;
import java.util.Map;

public interface StockAiService {
    Map<String, Object> getStockAi(String code, StockDto.Detail detail);
    void refreshDetailAi(String code, StockDto.Detail detail);
    void refreshYoutube(String keyword);
    //StockDto.YoutubeSearchResponse getYoutubeVideo(String keyword);

}
