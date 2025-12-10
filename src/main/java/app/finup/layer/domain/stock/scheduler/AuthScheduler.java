package app.finup.layer.domain.stock.scheduler;

import app.finup.layer.domain.stock.api.AuthStockApiClient;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * KIS API 사용을 위한 접큰 토큰 스케쥴러 자동 갱신
 * @author lky
 * @since 2025-12-10
 */
@Component
@RequiredArgsConstructor
@EnableScheduling
public class AuthScheduler {
    private final AuthStockApiClient authStockApiClient;

    @Scheduled(fixedRate = 1000 * 60 * 60 * 23)
    public void refreshAuth() {
        authStockApiClient.getToken();
    }
}
