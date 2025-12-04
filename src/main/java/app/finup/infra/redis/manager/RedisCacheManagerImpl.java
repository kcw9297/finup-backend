package app.finup.infra.redis.manager;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisCacheManagerImpl implements RedisCacheManager {

    private final StringRedisTemplate redis;

    @Override
    public void saveNews(String key, String json, long ttlMillis) {
        redis.opsForValue().set(key, json, ttlMillis, TimeUnit.MILLISECONDS);
    }

    @Override
    public String getNews(String key) {
        return redis.opsForValue().get(key);
    }

    @Override
    public void deleteNews(String key) {
        redis.delete(key);
    }

    @Override
    public boolean exists(String key) {
        return redis.hasKey(key);
    }
}
