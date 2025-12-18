package app.finup.layer.domain.stock.redis;

import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.enums.CandleType;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.*;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockStorageImpl implements StockStorage {

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> rt;
    private final ObjectMapper objectMapper;

    /* redis key */
    private static final String DETAIL_KEY = "stock:detail:";
    private static final String DETAIL_AI_KEY = "stock:detail-ai:";
    private static final String YOUTUBE_KEY = "stock:youtube:";
    private static final String CHART_KEY = "stock:chart:";
    private static final String CHART_AI_KEY = "stock:chart-ai:";

    //시가총액 리스트
    @Override
    public void setMarketCapRow(List<StockDto.MarketCapRow> marketCapRowList) {
        rt.opsForValue().set("marketCapRowList", marketCapRowList, Duration.ofHours(72).plusMinutes(30));
    }

    @Override
    public List<StockDto.MarketCapRow> getMarketCapRow() {
        //return (List<StockDto.MarketCapRow>) rt.opsForValue().get("marketCapRowList");

        Object value = rt.opsForValue().get("marketCapRowList");
        if (value == null) return Collections.emptyList();
        return objectMapper.convertValue(value, new TypeReference<List<StockDto.MarketCapRow>>(){});
    }

    //거래대금 리스트
    @Override
    public List<StockDto.TradingValueRow> getTradingValueRow() {
        Object value = rt.opsForValue().get("tradingValueRowList");
        if (value == null) return Collections.emptyList();
        return objectMapper.convertValue(value, new TypeReference<List<StockDto.TradingValueRow>>(){});
    }

    @Override
    public void setTradingValueRow(List<StockDto.TradingValueRow> tradingValueRowList) {
        rt.opsForValue().set("tradingValueRowList", tradingValueRowList, Duration.ofHours(72).plusMinutes(30));
    }

    //종목 상세 데이터
    @Override
    public void setDetail(String code, StockDto.Detail detail) {
        rt.opsForValue().set(DETAIL_KEY+code, detail, Duration.ofHours(72).plusMinutes(30));
    }

    @Override
    public StockDto.Detail getDetail(String code) {
        Object object  = rt.opsForValue().get(DETAIL_KEY+code);
        if (object == null) return null;
        return objectMapper.convertValue(object, StockDto.Detail.class);
    }

    //차트 탭
    @Override
    public void setChart(String code, CandleType candleType, StockChartDto.Row row) {
        rt.opsForValue().set(CHART_KEY+candleType+code, row, Duration.ofHours(72).plusMinutes(30));
    }

    @Override
    public StockChartDto.Row getChart(String code, CandleType candleType) {
        Object object  = rt.opsForValue().get(CHART_KEY+candleType+code);
        if (object == null) return null;
        return objectMapper.convertValue(object, StockChartDto.Row.class);
    }

    @Override
    public void setChartAi(String code, CandleType candleType, StockChartDto.ChartAi chartAi) {
        rt.opsForValue().set(CHART_AI_KEY+candleType+code, chartAi, Duration.ofHours(72).plusMinutes(30));
    }

    @Override
    public StockChartDto.ChartAi getChartAi(String code, CandleType candleType) {
        Object object  = rt.opsForValue().get(CHART_AI_KEY+candleType+code);
        if (object == null) return null;
        return objectMapper.convertValue(object, StockChartDto.ChartAi.class);
    }

    //종목 탭
    @Override
    public void setDetailAi(String code, Map<String, Object> detailAi) {
        rt.opsForValue().set(DETAIL_AI_KEY+code, detailAi, Duration.ofHours(72).plusMinutes(30));
    }

    @Override
    public Map<String, Object> getDetailAi(String code) {
        return ( Map<String, Object>) rt.opsForValue().get(DETAIL_AI_KEY+code);
    }

    @Override
    public void setYoutube(String keyword, List<StockDto.YoutubeVideo> youtube) {
        rt.opsForValue().set(YOUTUBE_KEY+keyword, youtube, Duration.ofHours(72).plusMinutes(30));
    }

    @Override
    public List<StockDto.YoutubeVideo> getYoutube(String keyword) {
        return (List<StockDto.YoutubeVideo>) rt.opsForValue().get(YOUTUBE_KEY+keyword);
    }
}
