package app.finup.layer.domain.studyword.redis;

import app.finup.layer.domain.studyword.constant.StudyWordCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * StudyWordRedisStorage 구현 클래스
 * @author kcw
 * @since 2025-12-16
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class StudyWordRedisStorageImpl implements StudyWordRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final Duration TTL_LATEST_KEYWORDS = Duration.ofHours(12); // 12시간 지속
    private static final int MAX_STORE_AMOUNT_ID = 5; // 5개 저장


    @Override
    public void storeLatestRecommendedIds(List<String> videoLinkIds, Long memberId) {

        // [1] key 생성
        String key = StudyWordCache.LATEST_RECOMMENDED_STUDY_WORD_IDS
                .replace("${MEMBER_ID}", String.valueOf(memberId));

        // [2] 현재 키 존재여부 확인
        Boolean exists = srt.hasKey(key);

        // [3] redis 내 저장
        srt.opsForList().leftPushAll(key, videoLinkIds);
        srt.opsForList().trim(key, 0, MAX_STORE_AMOUNT_ID - 1); // 최대 저장하는 개수
        if (!exists) srt.expire(key, TTL_LATEST_KEYWORDS); // 키가 존재하지 않으면 TTL 설정
    }


    @Override
    public List<Long> getLatestRecommendedIds(Long memberId) {

        // [1] key 생성
        String key = StudyWordCache.LATEST_RECOMMENDED_STUDY_WORD_IDS
                .replace("${MEMBER_ID}", String.valueOf(memberId));

        // [2] redis 내 저장된 최근 문자열 일괄 조회
        List<String> keywords = srt.opsForList().range(key, 0L, -1L);

        // 만약 조회에 실패한 경우, 빈 리스트 반환
        if (Objects.isNull(keywords)) return List.of();

        // [3] 조회된 모든 번호를 Long 타입으로 변환 후 반환
        return keywords.stream().map(Long::parseLong).toList();
    }

}
