package app.finup.layer.domain.videolink.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 학습 영상 캐싱 데이터 Key와 관련한 상수 제공 열거형 상수
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkRedisKey {

    // placeholder
    public static final String STOCK_CODE = "${STOCK_CODE}";
    public static final String MEMBER_ID = "${MEMBER_ID}";

    // Base Key
    private static final String KEY_PREFIX = "VIDEO_LINK:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;
    private static final String LOCK_PREFIX = "LOCK:" + KEY_PREFIX;
    private static final String CACHE_RECOMMEND = CACHE_PREFIX + "RECOMMEND:";
    private static final String KEY_LATEST_SENTENCE = KEY_PREFIX + "LATEST_SENTENCE:";
    private static final String KEY_LATEST_RECOMMENDED = KEY_PREFIX + "LATEST_RECOMMENDED:";
    private static final String CACHE_RECOMMEND_HOME = CACHE_RECOMMEND + "HOME:";
    private static final String LOCK_RECOMMEND_HOME = LOCK_PREFIX + "HOME:";

    // key name
    public static final String KEY_LATEST_SENTENCE_HOME = KEY_LATEST_SENTENCE + "HOME:${MEMBER_ID}";
    public static final String KEY_LATEST_RECOMMENDED_VIDEO_IDS = KEY_LATEST_RECOMMENDED + "VIDEO_IDS:${MEMBER_ID}";

    // cache Name
    public static final String CACHE_RECOMMEND_STUDY = CACHE_RECOMMEND + "STUDY";
    public static final String CACHE_RECOMMEND_HOME_LOGIN = CACHE_RECOMMEND_HOME + "LOGIN";
    public static final String CACHE_RECOMMEND_HOME_LOGOUT = CACHE_RECOMMEND_HOME + "LOGOUT";

    // Lock Name
    public static final String LOCK_RECOMMEND_HOME_LOGOUT = LOCK_PREFIX + "HOME:";


}
