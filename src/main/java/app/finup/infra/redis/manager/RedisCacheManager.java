package app.finup.infra.redis.manager;

public interface RedisCacheManager {

    void saveNews(String key, String json, long ttlMillis);

    String getNews(String key);

    void deleteNews(String key);

    boolean exists(String key);
}
