package app.finup.layer.domain.quiz.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 주식 종목 Cache 정보를 저장하기 위한 Key를 관리하는 상수 클래스
 * @author kcw
 * @since 2025-12-31
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class QuizRedisKey {

    // placeholder
    public static final String MEMBER_ID = "${MEMBER_ID}";

    // Base Key
    private static final String KEY_PREFIX = "QUIZ:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;


    // key name
    public static final String KEY_PREV_WORDS = KEY_PREFIX + "PREV_WORDS:${MEMBER_ID}";

    // cache Name
    public static final String CACHE_QUESTION = CACHE_PREFIX + "QUESTION:${MEMBER_ID}";



}
