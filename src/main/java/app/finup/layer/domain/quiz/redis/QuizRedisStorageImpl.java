package app.finup.layer.domain.quiz.redis;

import app.finup.common.utils.StrUtils;
import app.finup.layer.base.template.RedisCodeTemplate;
import app.finup.layer.domain.quiz.constant.QuizRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;


/**
 * qQuizRedisStorage 구현 클래스
 * @author kcw
 * @since 2025-01-10
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class QuizRedisStorageImpl implements QuizRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final int MAX_AMOUNT_PREV_WORDS = 200;
    private static final Duration TTL_PREV_WORDS = Duration.ofDays(7);


    @Override
    public void storePrevWordsIds(Long memberId, List<Long> quizIds) {

        RedisCodeTemplate.addPrevList(
                srt,
                getKey(QuizRedisKey.KEY_PREV_WORDS, memberId),
                quizIds,
                MAX_AMOUNT_PREV_WORDS,
                TTL_PREV_WORDS
        );
    }


    @Override
    public List<Long> getPrevWordsIds(Long memberId) {

        return RedisCodeTemplate.getPrevList(
                srt,
                getKey(QuizRedisKey.KEY_PREV_WORDS, memberId),
                Long::valueOf // Long 으로 변환
        );
    }


    // key 제공 메소드 (내부 placeholder 채움)
    private String getKey(String baseKey, Long memberId) {

        // 파라미터 Map
        Map<String, String> params = Map.of(QuizRedisKey.MEMBER_ID, String.valueOf(memberId));

        // key 생성 및 반환
        return StrUtils.fillPlaceholder(baseKey, params);
    }
}

