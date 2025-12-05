package app.finup.layer.domain.news.scheduler;

import app.finup.layer.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsScheduler {
    private final NewsService newsService;

    @Scheduled(fixedRate = 1000 * 60 * 30)
    public void updateNewsCache(){
        log.info("[SCHEDULER] 뉴스 캐시 자동 갱신 실행");
        newsService.refreshAllCategories();
    }
}

