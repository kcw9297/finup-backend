package app.finup.api.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import lombok.Builder;
import reactor.util.retry.Retry;
import reactor.util.retry.RetryBackoffSpec;

import java.time.Duration;

/**
 * 외부 REST API 요청 시, Retry 적용 설정을 돕는 Builder 클래스
 * @author kcw
 * @since 2026-01-14
 */

@Builder
public class ApiRetry {

    @Builder.Default
    private int attempts = 3; // 시도 횟수

    @Builder.Default
    private Duration minBackoff = Duration.ofMillis(1000); // 최소 대기시간

    @Builder.Default
    private Duration maxBackoff = Duration.ofMillis(3000); // 최대 대기시간

    @Builder.Default // 0.0 는 미적용. 1.0은 가장 큰 무작위성 부여
    private double jitter = 0.5; // 재시도 간격에 무작위성 추가 (동시에 시도가 몰리는 것을 방지)

    @Builder.Default
    private String loggingMessage = "API 요청"; // 로깅 메세지

    private Class<?> loggerClass; // 호출 클래스 정보

    @Builder.Default
    private AppStatus apiFailedStatus = AppStatus.API_REQUEST_FAILED; // 로깅 실패 상태


    /**
     * Builder 기반 설정된 RetrySpec 반환
     * @return RetryBackoffSpec 구현체
     */
    public RetryBackoffSpec toRetrySpec() {
        return Retry.backoff(attempts, minBackoff)
                .maxBackoff(maxBackoff)
                .jitter(jitter)
                .doBeforeRetry(signal ->
                        LogUtils.showInfo(loggerClass, "%s %d/%d", loggingMessage, signal.totalRetries() + 1, attempts)
                )
                .onRetryExhaustedThrow((backoffSpec, signal) -> {
                    LogUtils.showError(loggerClass, "%s 실패. 총 %d번 시도", loggingMessage, attempts);
                    return new ProviderException(apiFailedStatus);
                });
    }
}
