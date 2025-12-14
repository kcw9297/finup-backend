package app.finup.layer.domain.news.service;

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
                NewsDto.Summary result =
                        newsAiService.analyzeLight(description); // 기존 메서드

                newsRedisStorage.saveNews(aiKey, result, Duration.ofHours(6));

                return result;
            } finally {
                srt.delete(lockKey);
            }
        }

        // 3. 다른 요청이 분석 중 → 잠깐 대기
        return retryFetch(aiKey);
    }

    private NewsDto.Summary retryFetch(String aiKey) {
        for (int i = 0; i < 3; i++) { // 최대 600ms
            sleep(200);
            NewsDto.Summary cached =
                    newsRedisStorage.getNews(aiKey, NewsDto.Summary.class);
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
