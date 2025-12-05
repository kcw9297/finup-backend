package app.finup.common.exception;

import app.finup.common.enums.AppStatus;

/**
 * Provider 클래스 내 체크 예외를 감싸는 예외
 * @author kcw
 * @since 2025-12-05
 */
public class ProviderException extends AppException {

    public ProviderException(AppStatus appStatus) {
        super(appStatus);
    }

    public ProviderException(AppStatus appStatus, Throwable cause) {
        super(appStatus, cause);
    }
}
