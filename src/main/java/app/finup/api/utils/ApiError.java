package app.finup.api.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import lombok.Builder;

import java.util.Objects;
import java.util.function.Function;

/**
 * 외부 REST API 요청 시, 실패 시 예외 처리 적용 설정을 돕는 Builder 클래스
 * @author kcw
 * @since 2026-01-14
 */

@Builder
public class ApiError {

    @Builder.Default
    private boolean showLog = true; // 로그 출력

    @Builder.Default
    private boolean preserveProviderException = true; // ProviderException 그대로 반환 여부

    @Builder.Default
    private String message = "API 요청에 실패했습니다."; // 로깅 메세지

    private Class<?> loggerClass; // 로깅 클래스

    @Builder.Default
    private AppStatus apiFailedStatus = AppStatus.API_REQUEST_FAILED; // 로깅 실패 상태

    private String fieldName; // API 실패 시, 사용자에게 표시할 FrontEnd 필드명

    /**
     * API 실패 처리를 위한 Function 생성
     * @return 에러 처리 함수
     */
    public Function<Exception, Throwable> toErrorFunction() {

        return ex -> {

            // 로그 출력
            if (showLog) LogUtils.showError(loggerClass, "%s 원인: %s", message, ex.getMessage());

            // ProviderException은 그대로 전파
            if (preserveProviderException && ex instanceof ProviderException) return ex;

            // 그 외 예상 밖의 오류인 경우 처리
            return Objects.isNull(fieldName) ?
                    new ProviderException(apiFailedStatus, ex) :
                    new ProviderException(apiFailedStatus, fieldName, ex);
        };
    }
}
