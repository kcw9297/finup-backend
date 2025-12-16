package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.component.NewsContentExtractor;
import app.finup.layer.domain.news.component.NewsIdGenerator;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.util.NewsRedisKeys;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Service;
import java.time.Duration;


@Slf4j
@Service
@RequiredArgsConstructor
public class StockNewsAiServiceImpl implements StockNewsAiService {
    private final NewsIdGenerator newsIdGenerator;
    private final NewsAiService newsAiService;
    private final NewsRedisStorage newsRedisStorage;
    private final StringRedisTemplate srt;
    private final NewsContentExtractor newsContentExtractor;

    @Override
    public Object analyzeAuto(String link, String description) {

        //  DEEP 가능 여부 판단
        if (canDeepAnalyze(link)) {
            NewsDto.Ai deep = analyzeDeepCached(link);
            if (deep != null) {
                return deep;
            }
        }

        //  DEEP 불가 or 실패 → LIGHT
        return analyzeLightCached(link, description);
    }

    private boolean canDeepAnalyze(String link) {
        return newsContentExtractor.isSupported(link);
    }

    @Override
    public NewsDto.Summary analyzeLightCached(String link, String description) {
        String newsId = newsIdGenerator.generate(link);
        String aiKey = NewsRedisKeys.aiLight(newsId);
        String lockKey = NewsRedisKeys.aiLock(newsId);

        // 1. 캐시 조회
        NewsDto.Summary cached = newsRedisStorage.getNews(aiKey, NewsDto.Summary.class);
        if (cached != null) return cached;

        // 2. 락 시도
        Boolean locked = srt.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(60));

        if (Boolean.TRUE.equals(locked)) {
            try {
                NewsDto.Summary result = newsAiService.analyzeLight(description); // 기존 메서드

                newsRedisStorage.saveNews(aiKey, result, Duration.ofHours(6));

                return result;
            } finally {
                srt.delete(lockKey);
            }
        }

        // 3. 다른 요청이 분석 중 → 잠깐 대기
        return retryFetch(aiKey, NewsDto.Summary.class, 3, 200);
    }


    @Override
    public NewsDto.Ai analyzeDeepCached(String link) {
        String newsId = newsIdGenerator.generate(link);
        String aiKey = NewsRedisKeys.aiDeep(newsId);
        String lockKey = NewsRedisKeys.aiLock(newsId);

        // 1. 캐시 조회
        NewsDto.Ai cached = newsRedisStorage.getNews(aiKey, NewsDto.Ai.class);
        if (cached != null) return cached;

        // 2. 락 시도
        Boolean locked = srt.opsForValue().setIfAbsent(lockKey, "1", Duration.ofSeconds(60));

        if (Boolean.TRUE.equals(locked)) {
            try {
                NewsDto.Ai result = newsAiService.analyzeDeep(link); // 기존 메서드

                newsRedisStorage.saveNews(aiKey, result, Duration.ofHours(6));

                return result;
            } finally {
                srt.delete(lockKey);
            }
        }

        // 3. 다른 요청이 분석 중 → 잠깐 대기
        return retryFetch(aiKey, NewsDto.Ai.class, 3, 200);
    }



    private <T> T retryFetch(String aiKey,Class<T> clazz, int retryCount, long sleepMillis) {
        for (int i = 0; i < retryCount; i++) { // 최대 600ms
            sleep(sleepMillis);
            T cached = newsRedisStorage.getNews(aiKey, clazz);
            if (cached != null) return cached;
        }
        return null;
    }


    private void sleep(long millis){
        try{
            Thread.sleep(millis);
        }catch (InterruptedException e){
            Thread.currentThread().interrupt();
        }
    }
}
