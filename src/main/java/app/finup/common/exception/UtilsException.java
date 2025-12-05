package app.finup.common.exception;

import app.finup.common.enums.AppStatus;

/**
 * Utils 클래스 내 체크 예외를 감싸거나, 반드시 알려야 하는 예외 상황을 처리하는 예외 클래스
 * @author kcw
 * @since 2025-11-26
 */
public class UtilsException extends AppException {

    public UtilsException(AppStatus appStatus) {
        super(appStatus);
    }

    public UtilsException(AppStatus appStatus, Throwable cause) {
        super(appStatus, cause);
    }
}
