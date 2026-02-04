package app.finup.layer.domain.news.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 뉴스 Cache 정보를 저장하기 위한 Key를 관리하는 상수 클래스
 * @author kcw
 * @since 2025-12-31
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsRedisKey {

    // placeholder
    public static final String NEWS_ID = "${NEWS_ID}";
    public static final String MEMBER_ID = "${MEMBER_ID}";

    // Base Key
    private static final String KEY_PREFIX = "NEWS:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;
    private static final String LOCK_PREFIX = "LOCK:" + KEY_PREFIX;
    // key name
    public static final String KEY_ANALYZE = KEY_PREFIX + "ANALYZE:${NEWS_ID}:${MEMBER_ID}";

    // Cache Name
    public static final String CACHE_MAIN = CACHE_PREFIX + "MAIN";
    public static final String CACHE_STOCK = CACHE_PREFIX + "STOCK";
    public static final String CACHE_ANALYZE = CACHE_PREFIX + "ANALYZE";

    // Lock Name
    public static final String LOCK_SYNC = LOCK_PREFIX + "SYNC";


}
