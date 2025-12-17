package app.finup.layer.domain.studyword.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 학습 영상 캐싱 데이터 Key와 관련한 상수 제공 열거형 상수
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyWordCache {

    public static final String CACHE_BASE = "CACHE:STUDY_WORD:";
    public static final String RECOMMEND_STUDY = CACHE_BASE + "RECOMMEND:STUDY";
    public static final String LATEST_RECOMMENDED_STUDY_WORD_IDS = CACHE_BASE + "LATEST_RECOMMENDED:STUDY_WORD_IDS::${MEMBER_ID}";

}
