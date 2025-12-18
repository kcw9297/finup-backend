package app.finup.layer.domain.indexMarket.scheduler;

import app.finup.layer.domain.indexMarket.service.IndexMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexMarketScheduler {
    private final IndexMarketService indexMarketService;

    // 자동 갱신
    @Scheduled(cron = "0 */10 15-18 ? * MON-FRI")
    @Async("schedulerExecutor")
    public void updateMarketIndex() {
        indexMarketService.updateIndexes();
    }
}