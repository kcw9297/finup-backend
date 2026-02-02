package app.finup.api.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import lombok.Builder;

import java.time.Duration;
import java.util.Random;

/**
 * 외부 REST API 요청 시, API 호출 "전" 대기 시간 적용 설정을 돕는 Builder 클래스
 * @author kcw
 * @since 2026-01-14
 */

@Builder
public class ApiDelay {

    // 사용 상수
    private static final Random RANDOM = new Random();

    @Builder.Default
    private Duration baseDelay = Duration.ofMillis(200); // 기본 대기시간

    @Builder.Default
    private Duration minDelay = Duration.ofMillis(200); // 최소 대기시간 (보장받는 최소 대기)

    @Builder.Default
    private Duration maxDelay = Duration.ofMillis(1000); // 최대 대기시간 (가능한 최대 대기)

    @Builder.Default // 0.0 는 미적용. 1.0은 가장 큰 무작위성 부여
    private double jitter = 0.5; // 재시도 간격에 무작위성 추가 (동시에 시도가 몰리는 것을 방지)

    @Builder.Default
    private AppStatus interruptedStatus = AppStatus.API_REQUEST_FAILED; // 로깅 실패 상태


    /**
     * Delay 수행
     */
    public void delay() {

        try {
            // 시간 계산
            long jitter = minDelay.toMillis() + RANDOM.nextLong(maxDelay.toMillis() - minDelay.toMillis() + 1);
            long totalDelay = baseDelay.toMillis() + jitter;

            // 대기 수행
            Thread.sleep(totalDelay);

            // 인터럽트 발생 처리
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new ProviderException(interruptedStatus);
        }
    }
}
