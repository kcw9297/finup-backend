package app.finup.layer.domain.news.redis;

import app.finup.common.utils.StrUtils;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsRedisStorageImpl implements NewsRedisStorage {

    private final StringRedisTemplate srt;

    @Override
    public void saveNews(String key, Object data, Duration ttl) {
        srt.opsForValue().set(key, StrUtils.toJson(data), ttl);
    }

    @Override
    public <T> T getNews(String key, Class<T> clazz) {
        String json = srt.opsForValue().get(key);
        return Objects.isNull(json) ? null : StrUtils.fromJson(json, clazz);
    }

    @Override
    public <T> T getNews(String key, TypeReference<T> type) {
        String json = srt.opsForValue().get(key);
        return Objects.isNull(json) ? null : StrUtils.fromJson(json, type);
    }

    @Override
    public void deleteNews(String key) {
        srt.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return srt.hasKey(key);
    }
}
