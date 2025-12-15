package app.finup.layer.domain.news.util;

public class NewsRedisKeys {
    public static String aiLight(String newsId) {
        return "NEWS:AI:LIGHT:" + newsId;
    }

    public static String aiDeep(String newsId) {
        return "NEWS:AI:DEEP:" + newsId;
    }

    public static String aiLock(String newsId) {
        return "LOCK:NEWS:AI:" + newsId;
    }
}
