package app.finup.layer.domain.news.redis;

import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Map;

/**
 * NewsRedisStorage 구현 클래스
 * @author kcw
 * @since 2026-01-07
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NewsRedisStorageImpl implements NewsRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final Duration TTL_ANALYZE = Duration.ofHours(1); // 종목 상세 정보


    @Override
    public void storePrevAnalyze(Long newsId, Long memberId, String analyzation) {
        srt.opsForValue().set(getKey(NewsRedisKey.KEY_ANALYZE, newsId, memberId), analyzation, TTL_ANALYZE);
    }


    @Override
    public String getPrevAnalyze(Long newsId, Long memberId) {
        return srt.opsForValue().get(getKey(NewsRedisKey.KEY_ANALYZE, newsId, memberId));
    }


    // key 조립 수행
    private String getKey(String baseKey, Long newsId, Long memberId) {

        return StrUtils.fillPlaceholder(
                baseKey,
                Map.of(NewsRedisKey.NEWS_ID, String.valueOf(newsId), NewsRedisKey.MEMBER_ID, String.valueOf(memberId))
        );
    }
}
