package app.finup.layer.domain.stock.redis;

import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.enums.CandleType;

import java.util.*;

/**
 * stock 관련 데이터 캐싱을 위한 저장 인터페이스
 * @author lky
 * @since 2025-12-12
 */
public interface StockStorage {

    // 종목+탭 시가총액 순위
    void setMarketCapRow(List<StockDto.MarketCapRow> marketCapRowList);
    List<StockDto.MarketCapRow> getMarketCapRow();

    // 종목+탭 거래대금 순위
    void setTradingValueRow(List<StockDto.TradingValueRow> tradingValueRowList);
    List<StockDto.TradingValueRow> getTradingValueRow();

    // 종목 상세 페이지 종목 데이터
    void setDetail(String code, StockDto.Detail detail);
    StockDto.Detail getDetail(String code);

    // 종목 상세 페이지 차트
    void setChart(String code, StockChartDto.Row row);
    StockChartDto.Row getChart(String code);

    // 종목 상세 페이지 차트 AI 분석
    void setChartAi(String code, StockChartDto.ChartAi chartAi);
    StockChartDto.ChartAi getChartAi(String code);

    // 종목 상세 페이지 종목 AI분석
    void setDetailAi(String code, Map<String, Object> detailAi);
    Map<String, Object> getDetailAi(String code);

    // 종목 상세 페이지 종목 추천영상
    void setYoutube(String keyword, List<StockDto.YoutubeVideo> youtube);
    List<StockDto.YoutubeVideo> getYoutube(String keyword);
}
