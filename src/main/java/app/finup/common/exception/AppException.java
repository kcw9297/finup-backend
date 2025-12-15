package app.finup.common.exception;

import app.finup.common.enums.AppStatus;
import lombok.Getter;

import java.util.Map;

/**
 * 애플리케이션 전반에서 발생한 예외 처리 (커스텀 예외 중 최상위 예외)
 * <br>사용 코드 목록 : {@link AppStatus}
 * @author kcw
 * @since 2025-11-26
 */

@Getter
public class AppException extends RuntimeException {

    private AppStatus appStatus; // 애플리케이션 상태 값 (AppStatus 참고)
    private Map<String, String> inputErrors; // 유효성 검사에 실패한 값 목록 (닉네임, 이메일, 인증코드, ...)

    public AppException(AppStatus appStatus, Map<String, String> inputErrors) {
        super();
        this.appStatus = appStatus;
        this.inputErrors = inputErrors;
    }

    public AppException(AppStatus appStatus) {
        super(appStatus.getMessage());
        this.appStatus = appStatus;
    }

    public AppException(AppStatus appStatus, String fieldName) {
        super(appStatus.getMessage());
        this.appStatus = appStatus;
        this.inputErrors = Map.of(fieldName, appStatus.getMessage());
    }

    public AppException(AppStatus appStatus, Throwable cause) {
        super(appStatus.getMessage(), cause);
        this.appStatus = appStatus;
    }

    public AppException() {}
}
