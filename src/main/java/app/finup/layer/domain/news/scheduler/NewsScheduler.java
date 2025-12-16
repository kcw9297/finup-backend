package app.finup.layer.domain.news.scheduler;

import app.finup.infra.news.provider.NewsProvider;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;


@Slf4j
@Component
@Profile("prod") //잠깐 비활성화
@RequiredArgsConstructor
public class NewsScheduler {
    private final NewsProvider newsProvider;
    private final NewsRedisStorage newsRedisStorage;
    private static final Duration TTL_NEWS = Duration.ofMinutes(30);

    @Scheduled(fixedRate = 1000 * 60 * 15)
    public void updateNewsCache(){
        refresh("date", 30);
        refresh("sim", 30);
        log.info("[SCHEDULER] 뉴스 캐시 갱신 완료");
    }

    private void refresh(String category, int limit) {
        String key = "NEWS:CATEGORY:" + category + ":" + limit;

        List<NewsDto.Row> fresh = newsProvider.fetchNews(category, limit);

        if(isComplete(fresh)){
            newsRedisStorage.saveNews(key, fresh, TTL_NEWS);
            log.info("[SCHEDULER] SAVE {}", key);
        }else{
            log.warn("[SCHEDULER] SKIP SAVE (incomplete) {}", key);
        }
    }

    private boolean isComplete(List<NewsDto.Row> list) {
        return list != null && list.size() >= 5;
    }
}

