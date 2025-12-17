package app.finup.common.exception;

import app.finup.common.enums.AppStatus;

/**
 * Manager 클래스 내 체크 예외를 감싸는 예외
 * @author kcw
 * @since 2025-11-26
 */
public class ManagerException extends AppException {

    public ManagerException(AppStatus appStatus) {
        super(appStatus);
    }

    public ManagerException(AppStatus appStatus, String fieldName) {
        super(appStatus, fieldName);
    }

    public ManagerException(AppStatus appStatus, Throwable cause) {
        super(appStatus, cause);
    }

    public ManagerException(AppStatus appStatus, String fieldName, Throwable cause) {
        super(appStatus, fieldName, cause);
    }
}
