package app.finup.common.exception;

import app.finup.common.enums.AppStatus;
import lombok.Getter;

/*
 * [수정 이력]
 *  ▶ ver 1.0 (2025-11-24) : kcw97 최초 작성
 */

/**
 * 비즈니스 로직 예외
 * @author kcw
 * @since 2025-11-26
 */

@Getter
public class BusinessException extends AppException {

    // AppStatus 내 기본 메세지와 상태가 아닌, 직접 전달해야 하는 경우
    private String message;
    private int statusCode;

    /**
     * 비즈니스 예외 상황 전파 (AppStatus 이용)
     * @param appStatus 애플리케이션 상태 상수
     */
    public BusinessException(AppStatus appStatus) {
        super(appStatus);
    }

    /**
     * 직접 비즈니스 예외 상황 전파 (오류 메세지와 상태 코드 수동 작성)
     * @param message 오류 메세지
     * @param statusCode 오류 코드
     */
    public BusinessException(String message, int statusCode) {
        super();
        this.message = message;
        this.statusCode = statusCode;
    }
}
