package app.finup.layer.domain.words.redis;

import app.finup.common.utils.StrUtils;
import app.finup.layer.base.template.RedisCodeTemplate;
import app.finup.layer.domain.words.constant.WordsRedisKey;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;


@Slf4j
@Component
@RequiredArgsConstructor
public class WordsRedisStorageImpl implements WordsRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final Duration TTL_RECENT_SEARCH_KEYWORDS = Duration.ofDays(14);
    private static final Duration TTL_KEY_PREV_RECOMMENDATION_NEWS = Duration.ofDays(2);

    // 최대 저장 개수
    private static final int MAX_AMOUNT_RECENT_SEARCH_KEYWORD = 10;
    private static final int MAX_KEY_PREV_RECOMMENDATION_NEWS = 15;

    @Override
    public void storeRecentSearchKeyword(Long memberId, String keyword) {
        srt.opsForValue().set("debug:recent:test", "OK");


        String key = createKey(memberId);
        Long score = System.currentTimeMillis();

        // [1] 동일 키워드 제거 (중복 방지)
        srt.opsForZSet().remove(key, keyword);

        // [2] 최신 검색어 추가
        srt.opsForZSet().add(key, keyword, score);

        // [3] 개수 초과 시 오래된 것 제거
        Long size = srt.opsForZSet().zCard(key);
        if (size != null && size > MAX_AMOUNT_RECENT_SEARCH_KEYWORD) {
            srt.opsForZSet().removeRange(key, 0, size - MAX_AMOUNT_RECENT_SEARCH_KEYWORD - 1);
        }

        // [4] TTL 설정
        srt.expire(key, TTL_RECENT_SEARCH_KEYWORDS);
        log.info(
                "[RECENT SEARCH TTL] key={}, ttlSeconds={}",
                key,
                TTL_RECENT_SEARCH_KEYWORDS.getSeconds()
        );

    }


    @Override
    public List<String> getRecentSearchKeywords(Long memberId, Integer limit) {
        String key = createKey(memberId);
        log.info("[RECENT SEARCH] redisKey={}", createKey(memberId));

        return srt.opsForZSet()
                .reverseRange(key, 0, limit - 1)
                .stream()
                .toList();
    }


    @Override
    public void removeRecentSearchKeyword(Long memberId, String keyword) {
        String key = createKey(memberId);
        srt.opsForZSet().remove(key, keyword);
    }


    @Override
    public void clearRecentSearchKeywords(Long memberId) {
        srt.delete(createKey(memberId));
    }

    private String createKey(Long memberId) {
        return "recentKeyword:search:%d".formatted(memberId);
    }


    @Override
    public void storePrevRecommendationIds(Long newsId, Long memberId, List<Long> termIds) {

        RedisCodeTemplate.addPrevList(
                srt,
                getRecommendationKey(newsId, memberId),
                termIds,
                MAX_KEY_PREV_RECOMMENDATION_NEWS,
                TTL_KEY_PREV_RECOMMENDATION_NEWS
        );
    }

    @Override
    public List<Long> getPrevRecommendationIds(Long newsId, Long memberId) {

        return RedisCodeTemplate.getPrevList(
                srt,
                getRecommendationKey(newsId, memberId),
                Long::valueOf // Long 으로 변환
        );
    }


    // key 제공 메소드 (내부 placeholder 채움)
    private String getRecommendationKey(Long newsId, Long memberId) {

        // 파라미터 Map
        Map<String, String> params = Map.of(
                WordsRedisKey.NEWS_ID, String.valueOf(newsId),
                WordsRedisKey.MEMBER_ID, String.valueOf(memberId)
        );

        // key 생성 및 반환
        return StrUtils.fillPlaceholder(WordsRedisKey.KEY_PREV_RECOMMENDATION_NEWS, params);
    }
}
