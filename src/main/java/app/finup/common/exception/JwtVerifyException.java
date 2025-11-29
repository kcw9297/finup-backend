package app.finup.common.exception;

import app.finup.common.enums.AppStatus;

/**
 * JWT 토큰 Refresh 실패 처리 예외
 * @author kcw
 * @since 2025-11-26
 */
public class JwtVerifyException extends AppException {

    public JwtVerifyException(AppStatus appStatus) {
        super(appStatus);
    }

    public JwtVerifyException(AppStatus appStatus, Throwable cause) {
        super(appStatus, cause);
    }
}
