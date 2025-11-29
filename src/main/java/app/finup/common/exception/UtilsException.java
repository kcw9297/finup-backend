package app.finup.common.exception;

import app.finup.common.enums.AppStatus;

/*
 * [수정 이력]
 *  ▶ ver 1.0 (2025-11-24) : kcw97 최초 작성
 */

/**
 * Utils 클래스 내 체크 예외를 감싸는 예외
 * @author kcw
 * @since 2025-11-26
 */
public class UtilsException extends AppException {

    public UtilsException(AppStatus appStatus, Throwable cause) {
        super(appStatus, cause);
    }
}
