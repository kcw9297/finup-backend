package app.finup.layer.domain.indexMarket.scheduler;

import app.finup.layer.domain.indexMarket.service.IndexMarketService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.boot.context.event.ApplicationReadyEvent;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndexMarketStartupRunner {
    private final IndexMarketService indexMarketService;

    // 서버 시작 시 지수 자동 갱신 트리거
    @EventListener(ApplicationReadyEvent.class)
    public void runOnStartup() {
        log.info("서버 시작 → 지수 자동 갱신 시도");
        indexMarketService.updateIndexes();
        log.info("서버 시작 → 지수 자동 갱신 완료");
    }
}