package app.finup.layer.domain.words.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;


@Slf4j
@Component
@RequiredArgsConstructor
public class RedisRecentSearchStorageImpl implements RedisRecentSearchStorage {

    private final StringRedisTemplate srt;

    // 최근 검색어 유지 시간 (예: 7일)
    private static final Duration TTL = Duration.ofDays(7);

    // 최대 저장 개수
    private static final int MAX_SIZE = 10;

    @Override
    public void add(Long memberId, String keyword) {
        srt.opsForValue().set("debug:recent:test", "OK");


        String key = createKey(memberId);
        Long score = System.currentTimeMillis();

        // [1] 동일 키워드 제거 (중복 방지)
        srt.opsForZSet().remove(key, keyword);

        // [2] 최신 검색어 추가
        srt.opsForZSet().add(key, keyword, score);

        // [3] 개수 초과 시 오래된 것 제거
        Long size = srt.opsForZSet().zCard(key);
        if (size != null && size > MAX_SIZE) {
            srt.opsForZSet().removeRange(key, 0, size - MAX_SIZE - 1);
        }

        // [4] TTL 설정
        srt.expire(key, TTL);
        log.info(
                "[RECENT SEARCH TTL] key={}, ttlSeconds={}",
                key,
                TTL.getSeconds()
        );

    }



    @Override
    public List<String> getRecent(Long memberId, Integer limit) {
        String key = createKey(memberId);
        log.info("[RECENT SEARCH] redisKey={}", createKey(memberId));

        return srt.opsForZSet()
                .reverseRange(key, 0, limit - 1)
                .stream()
                .toList();
    }


    @Override
    public void remove(Long memberId, String keyword) {
        String key = createKey(memberId);
        srt.opsForZSet().remove(key, keyword);
    }

    @Override
    public void clear(Long memberId) {
        srt.delete(createKey(memberId));
    }

    private String createKey(Long memberId) {
        return "recentKeyword:search:%d".formatted(memberId);
    }
}
