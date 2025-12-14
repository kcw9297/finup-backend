package app.finup.layer.domain.indexMarket.scheduler;

import app.finup.layer.domain.indexMarket.service.IndexMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexMarketScheduler {
    private final IndexMarketService indexMarketService;

    // 장 마감 이후 지수 스냅샷 저장용 스케줄러
    @Scheduled(cron = "0 10 16 ? * MON-FRI")
    public void updateMarketIndex() {
        log.info("지수 자동 갱신 시작");
        indexMarketService.updateIndexes();
        log.info("지수 자동 갱신 완료");
    }
}