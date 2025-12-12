package app.finup.layer.domain.exchangeRate.scheduler;

import app.finup.layer.domain.exchangeRate.service.ExchangeRateService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class ExchangeRateScheduler {
    private final ExchangeRateService exchangeRateService;

    // 평일 오전 11시 5분 환율 자동 갱신
    @Scheduled(cron = "0 5 11 ? * MON-FRI")
    public void updateExchangeRate() {
        log.info("환율 자동 갱신 시작");
        exchangeRateService.updateRates();
        log.info("환율 자동 갱신 완료");
    }
}