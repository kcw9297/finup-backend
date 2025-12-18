package app.finup.layer.domain.exchangeRate.scheduler;

import app.finup.layer.domain.exchangeRate.service.ExchangeRateService;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateScheduler {
    private final ExchangeRateService exchangeRateService;

    // 서버 시작 시 1회 환율 갱신
    @PostConstruct
    public void init() {
        log.info("서버 시작 → 환율 초기 갱신");
        exchangeRateService.updateRates();
    }

    // 평일 오전 11시 5분 환율 자동 갱신
    @Scheduled(cron = "0 5 11 ? * MON-FRI")
    @Async("schedulerExecutor")
    public void updateExchangeRate() {
        log.info("환율 자동 갱신 시작");
        exchangeRateService.updateRates();
        log.info("환율 자동 갱신 완료");
    }
}