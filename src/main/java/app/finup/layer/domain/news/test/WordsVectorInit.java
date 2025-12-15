package app.finup.layer.domain.news.test;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class WordsVectorInit {
    private final WordsVectorIngestService ingestService;
    private final StringRedisTemplate redisTemplate;

    @PostConstruct
    public void init() {
        Boolean already = redisTemplate.hasKey("vector:words:ingested");

        if (Boolean.TRUE.equals(already)) {
            log.info("⏭ Words Vector 이미 적재됨 → skip");
            return;
        }

        log.info("🚀 Words Vector 적재 시작");
        ingestService.ingestAllWords();

        redisTemplate.opsForValue().set(
                "vector:words:ingested",
                "true"
        );

        log.info("✅ Words Vector 적재 완료 (Redis flag set)");
    }
}
