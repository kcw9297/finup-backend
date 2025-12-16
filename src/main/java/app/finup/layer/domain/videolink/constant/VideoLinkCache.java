package app.finup.layer.domain.videolink.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 학습 영상 캐싱 데이터 Key와 관련한 상수 제공 열거형 상수
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkCache {

    public static final String CACHE_BASE = "CACHE:VIDEO_LINK:";
    public static final String RECOMMEND_HOME_LOGIN = CACHE_BASE + "RECOMMEND:LOGIN";
    public static final String RECOMMEND_HOME_LOGOUT = CACHE_BASE + "RECOMMEND:LOGOUT";
    public static final String RECOMMEND_STUDY = CACHE_BASE + "RECOMMEND:STUDY";
    public static final String LATEST_KEYWORDS_HOME = CACHE_BASE + "LATEST_KEYWORDS::${MEMBER_ID}";
    public static final String LATEST_KEYWORDS_STUDY = CACHE_BASE + "LATEST_KEYWORDS::${STUDY_ID}:${MEMBER_ID}";

}
