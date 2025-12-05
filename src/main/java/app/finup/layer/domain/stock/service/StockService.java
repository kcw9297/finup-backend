package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.stock.dto.StocksDto;

public interface StockService {
    //List<StocksDto.Row> getMarketCapRanking();
    //List<StocksDto> getStocks();
    void importKospi() throws Exception;
    StocksDto.Detail getDetail(String code);
}
