package app.finup.common.exception;

import app.finup.common.enums.AppStatus;

/**
 * 작업 수행을 위한 Lock 획득 실패 처리 시 발생 예외를 처리하는 클래스
 * @author kcw
 * @since 2026-01-05
 */
public class LockException extends AppException {

    public LockException(AppStatus appStatus) {
        super(appStatus);
    }

    public LockException(AppStatus appStatus, Throwable cause) {
        super(appStatus, cause);
    }
}
