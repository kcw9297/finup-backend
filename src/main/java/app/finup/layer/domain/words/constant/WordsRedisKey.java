package app.finup.layer.domain.words.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 단어와 관련한 상수 클래스
 * @author kcw
 * @since 2026-01-22
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordsRedisKey {

    // placeholder
    public static final String STOCK_CODE = "${STOCK_CODE}";
    public static final String NEWS_ID = "${NEWS_ID}";
    public static final String MEMBER_ID = "${MEMBER_ID}";

    // Base Key
    private static final String KEY_PREFIX = "WORDS:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;
    private static final String LOCK_PREFIX = "LOCK:" + KEY_PREFIX;
    private static final String KEY_RECOMMENDATION = KEY_PREFIX + "RECOMMENDATION:";
    private static final String KEY_PREV_RECOMMENDATION = KEY_RECOMMENDATION + "PREV:";
    private static final String CACHE_RECOMMENDATION = CACHE_PREFIX + "RECOMMENDATION:";


    // key name
    public static final String KEY_PREV_RECOMMENDATION_NEWS = KEY_PREV_RECOMMENDATION + "NEWS:${NEWS_ID}:${MEMBER_ID}";

    // cache Name
    public static final String CACHE_TODAY_WORDS = CACHE_PREFIX + "TODAY_WORDS";
    public static final String CACHE_SEARCH = CACHE_PREFIX + "SEARCH";
    public static final String CACHE_RECOMMENDATION_NEWS = CACHE_RECOMMENDATION + "NEWS";

}
