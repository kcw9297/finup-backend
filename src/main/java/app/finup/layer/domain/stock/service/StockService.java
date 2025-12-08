package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.stock.dto.StockDto;
import java.util.List;

public interface StockService {
    // 종목 상세페이지 시가총액 순위 가져오기
    List<StockDto.MarketCapRow> getMarketCapRow();

    //List<StocksDto> getStocks();
    void importKospi() throws Exception;
    StockDto.Detail getDetail(String code);
}
