package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.stock.dto.StockDto;
import com.fasterxml.jackson.databind.JsonNode;

import java.util.List;

public interface StockService {
    // 종목+탭 시가총액 순위 가져오기
    List<StockDto.MarketCapRow> getMarketCapRow();
    void refreshMarketCapRow();

    // 종목+탭 거래대금 순위 가져오기
    List<StockDto.TradingValueRow> getTradingValueRow();
    void refreshTradingValueRow();

    //List<StocksDto> getStocks();
    void importKospi() throws Exception;
    StockDto.Detail getDetail(String code);
    void refreshDetail(String code);

    List<NewsDto.Row> getStockNews(String stockName, String category);






}
