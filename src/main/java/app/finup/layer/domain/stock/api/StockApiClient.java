package app.finup.layer.domain.stock.api;

import app.finup.layer.domain.stock.dto.StockDto;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

/**
 * KIS API 호출 및 Json파싱 인터페이스
 * @author lky
 * @since 2025-12-08
 */
public interface StockApiClient {
    List<StockDto.MarketCapRow> fetchMarketCapRow();
    List<StockDto.TradingValueRow> fetchTradingValueRow();
    JsonNode fetchDetail(String code);
    StockDto.YoutubeSearchResponse fetchYoutubeVideo(String keyword);
}
