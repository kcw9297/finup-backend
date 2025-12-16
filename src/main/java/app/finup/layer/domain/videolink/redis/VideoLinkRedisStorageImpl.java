package app.finup.layer.domain.videolink.redis;

import app.finup.layer.domain.videolink.constant.VideoLinkCache;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * VideoLinkRedisStorage 구현 클래스
 * @author kcw
 * @since 2025-12-16
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoLinkRedisStorageImpl implements VideoLinkRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final Duration TTL_LATEST_KEYWORDS = Duration.ofHours(6); // 1시간 지속
    private static final int MAX_KEYWORDS_HOME = 2;
    private static final int MAX_KEYWORDS_STUDY = 1;

    @Override // TODO 기존 @Cacheable 결과를 덮어쓰게 해야함
    public void storeLatestKeywordsForHome(String keywords, Long memberId) {

        // [1] key 생성
        String key = VideoLinkCache.LATEST_KEYWORDS_HOME
                .replace("${MEMBER_ID}", String.valueOf(memberId));

        // [2] 현재 키 존재여부 확인
        Boolean exists = srt.hasKey(key);

        // [3] redis 내 저장
        srt.opsForList().leftPush(key, keywords);
        srt.opsForList().trim(key, 0, MAX_KEYWORDS_HOME - 1); // 최대 저장하는 개수
        if (!exists) srt.expire(key, TTL_LATEST_KEYWORDS); // 키가 존재하지 않으면 TTL 설정
    }


    @Override
    public String getLatestKeywordsForHome(Long memberId) {

        // [1] key 생성
        String key = VideoLinkCache.LATEST_KEYWORDS_HOME
                .replace("${MEMBER_ID}", String.valueOf(memberId));

        // [2] redis 내 저장된 최근 문자열 일괄 조회
        return getLatestKeywords(key);
    }


    @Override
    public void storeLatestKeywordsForStudy(String keywords, Long studyId, Long memberId) {

        // [1] key 생성
        String key = VideoLinkCache.LATEST_KEYWORDS_STUDY
                .replace("${STUDY_ID}", String.valueOf(studyId))
                .replace("${MEMBER_ID}", String.valueOf(memberId));

        // [2] 현재 키 존재여부 확인
        Boolean exists = srt.hasKey(key);

        // [2] redis 내 저장
        srt.opsForList().leftPush(key, keywords);
        srt.opsForList().trim(key, 0, MAX_KEYWORDS_STUDY - 1); // 최대 저장하는 개수
        if (!exists) srt.expire(key, TTL_LATEST_KEYWORDS); // 키가 존재하지 않으면 TTL 설정
    }


    @Override
    public String getLatestKeywordsForStudy(Long studyId, Long memberId) {

        // [1] key 생성
        String key = VideoLinkCache.LATEST_KEYWORDS_STUDY
                .replace("${STUDY_ID}", String.valueOf(studyId))
                .replace("${MEMBER_ID}", String.valueOf(memberId));

        return getLatestKeywords(key);
    }


    // 최근 키워드 일괄 조회
    private String getLatestKeywords(String key) {
        // [2] redis 내 저장된 최근 문자열 일괄 조회
        List<String> keywords = srt.opsForList().range(key, 0L, -1L);

        // 만약 키워드 조회에 실패한 경우, 빈 문자열 반환
        if (Objects.isNull(keywords)) return "";

        // [3] 조회된 모든 키워드를 조합 후 반환
        return String.join(" ", keywords);
    }
}
