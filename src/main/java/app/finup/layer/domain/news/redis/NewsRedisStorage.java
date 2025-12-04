package app.finup.layer.domain.news.redis;


import com.fasterxml.jackson.core.type.TypeReference;

import java.time.Duration;

public interface NewsRedisStorage {
    void saveNews(String key, Object data, Duration ttl);

    <T> T getNews(String key, Class<T> clazz);

    <T> T getNews(String key, TypeReference<T> type);

    void deleteNews(String key);

    boolean exists(String key);


}
