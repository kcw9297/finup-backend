package app.finup.layer.domain.stock.redis;

import app.finup.layer.domain.stock.dto.StockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockStorageImpl implements StockStorage {

    @Qualifier("redisTemplate")
    private final RedisTemplate<String, Object> srt;

    @Override
    public void setMarketCapRow(List<StockDto.MarketCapRow> marketCapRowList) {
        srt.opsForValue().set("marketCapRowList", marketCapRowList, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public List<StockDto.MarketCapRow> getMarketCapRow() {
        return (List<StockDto.MarketCapRow>) srt.opsForValue().get("marketCapRowList");
    }

    @Override
    public void setDetail(String code, StockDto.Detail detail) {
        srt.opsForValue().set(code, detail, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public StockDto.Detail getDetail(String code) {
        return (StockDto.Detail) srt.opsForValue().get(code);
    }

    @Override
    public void setDetailAi(String code, Map<String, Object> detailAi) {
        srt.opsForValue().set(code, detailAi, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public Map<String, Object> getDetailAi(String code) {
        return ( Map<String, Object>) srt.opsForValue().get(code);
    }

    @Override
    public void setYoutube(String keyword, List<StockDto.YoutubeVideo> youtube) {
        srt.opsForValue().set(keyword, youtube, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public List<StockDto.YoutubeVideo> getYoutube(String keyword) {
        return (List<StockDto.YoutubeVideo>) srt.opsForValue().get(keyword);
    }
}
