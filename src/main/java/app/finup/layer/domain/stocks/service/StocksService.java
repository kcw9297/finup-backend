package app.finup.layer.domain.stocks.service;

import app.finup.layer.domain.stocks.dto.StocksDto;
import java.util.List;

public interface StocksService {
    //List<StocksDto.Row> getMarketCapRanking();
    //List<StocksDto> getStocks();
    List<StocksDto.Detail> getDetail(String code);
}
