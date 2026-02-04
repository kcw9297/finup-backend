package app.finup.layer.domain.stock.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 주식 종목 Cache 정보를 저장하기 위한 Key를 관리하는 상수 클래스
 * @author kcw
 * @since 2025-12-31
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StockRedisKey {

    // placeholder
    public static final String STOCK_CODE = "${STOCK_CODE}";
    public static final String MEMBER_ID = "${MEMBER_ID}";

    // Base Key
    private static final String KEY_PREFIX = "STOCK:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;
    private static final String LOCK_PREFIX = "LOCK:" + KEY_PREFIX;
    private static final String KEY_RANK = KEY_PREFIX + "RANK:";
    private static final String KEY_ANALYZE = KEY_PREFIX + "ANALYZE:";
    private static final String KEY_RECOMMEND = KEY_PREFIX + "RECOMMEND:";
    private static final String CACHE_ANALYZE = CACHE_PREFIX + "ANALYZE:";
    private static final String CACHE_RECOMMEND = CACHE_PREFIX + "RECOMMEND:";

    // key name
    public static final String KEY_RANK_MARKET_CAP = KEY_RANK + "MARKET_CAP";
    public static final String KEY_RANK_TRADING_VALUE = KEY_RANK + "TRADING_VALUE";
    public static final String KEY_AT = KEY_PREFIX + "AT";
    public static final String KEY_NAMES = KEY_PREFIX + "NAMES";
    public static final String KEY_INFOS = KEY_PREFIX + "INFOS";
    public static final String KEY_ANALYZE_CHART = KEY_ANALYZE + "CHART:${STOCK_CODE}:${MEMBER_ID}";
    public static final String KEY_ANALYZE_DETAIL = KEY_ANALYZE + "DETAIL:${STOCK_CODE}:${MEMBER_ID}";
    public static final String KEY_RECOMMEND_YOUTUBE = KEY_RECOMMEND + "YOUTUBE:${STOCK_CODE}:${MEMBER_ID}";

    // cache Name
    public static final String CACHE_INFO = CACHE_PREFIX + "INFO:";
    public static final String CACHE_ANALYZE_CHART = CACHE_ANALYZE + "CHART";
    public static final String CACHE_ANALYZE_DETAIL = CACHE_ANALYZE + "DETAIL";
    public static final String CACHE_RECOMMEND_YOUTUBE = CACHE_RECOMMEND + "YOUTUBE";

    // Lock Name
    public static final String LOCK_SYNC = LOCK_PREFIX + "SYNC";
    public static final String LOCK_ISSUE_TOKEN = LOCK_PREFIX + "ISSUE_TOKEN";

}
