package app.finup.layer.domain.indicator.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 뉴스 Cache 정보를 저장하기 위한 Key를 관리하는 상수 클래스
 * @author kcw
 * @since 2025-12-31
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndicatorRedisKey {

    // Base Key
    private static final String KEY_PREFIX = "INDICATOR:";
    private static final String KEY_INDEX = KEY_PREFIX + "INDEX:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;
    private static final String LOCK_PREFIX = "LOCK:" + KEY_PREFIX;
    private static final String LOCK_SYNC = LOCK_PREFIX + "SYNC:";
    private static final String LOCK_SYNC_INDEX = LOCK_SYNC + "INDEX:";

    // Key Name
    public static final String KEY_INDEX_FINANCIAL = KEY_INDEX + "FINANCIAL";
    public static final String KEY_INDEX_MARKET = KEY_INDEX + "MARKET";

    // Lock Name
    public static final String LOCK_SYNC_INDEX_FINANCIAL = LOCK_SYNC_INDEX + "FINANCIAL";
    public static final String LOCK_SYNC_INDEX_MARKET = LOCK_SYNC_INDEX + "MARKET";

}
