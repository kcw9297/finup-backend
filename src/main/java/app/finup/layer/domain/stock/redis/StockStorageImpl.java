package app.finup.layer.domain.stock.redis;

import app.finup.layer.domain.stock.dto.StockDto;
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

    @Override
    public void setMarketCapRow(List<StockDto.MarketCapRow> marketCapRowList) {
        rt.opsForValue().set("marketCapRowList", marketCapRowList, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public List<StockDto.MarketCapRow> getMarketCapRow() {
        return (List<StockDto.MarketCapRow>) rt.opsForValue().get("marketCapRowList");
    }

    @Override
    public void setDetail(String code, StockDto.Detail detail) {
        rt.opsForValue().set(DETAIL_KEY+code, detail, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public StockDto.Detail getDetail(String code) {
        Object object  = rt.opsForValue().get(DETAIL_KEY+code);
        if (object == null) return null;
        return objectMapper.convertValue(object, StockDto.Detail.class);
    }

    @Override
    public void setDetailAi(String code, Map<String, Object> detailAi) {
        rt.opsForValue().set(DETAIL_AI_KEY+code, detailAi, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public Map<String, Object> getDetailAi(String code) {
        return ( Map<String, Object>) rt.opsForValue().get(DETAIL_AI_KEY+code);
    }

    @Override
    public void setYoutube(String keyword, List<StockDto.YoutubeVideo> youtube) {
        rt.opsForValue().set(YOUTUBE_KEY+keyword, youtube, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public List<StockDto.YoutubeVideo> getYoutube(String keyword) {
        return (List<StockDto.YoutubeVideo>) rt.opsForValue().get(YOUTUBE_KEY+keyword);
    }
}
