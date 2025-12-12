package app.finup.layer.domain.stock.redis;

import app.finup.layer.domain.stock.dto.StockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class StockStorageImpl implements StockStorage {

    private final StringRedisTemplate srt;

    @Override
    public void setMarketCapRow(List<StockDto.MarketCapRow> marketCapRowList) {
        //srt.opsForValue().set("marketCapRowList", marketCapRowList, Duration.ofHours(24).plusMinutes(30));
    }

    @Override
    public List<StockDto.MarketCapRow> getMarketCapRow() {
        return List.of();
    }

    @Override
    public void setDetail(StockDto.Detail detail) {

    }

    @Override
    public StockDto.Detail getDetail() {
        return null;
    }

    @Override
    public void setDetailAi(Map<String, Object> detailAi) {

    }

    @Override
    public Map<String, Object> getDetailAi() {
        return Map.of();
    }

    @Override
    public void setYoutube(List<StockDto.YoutubeVideo> youtube) {

    }

    @Override
    public List<StockDto.YoutubeVideo> getYoutube(List<String> keywordList) {
        return List.of();
    }
}
