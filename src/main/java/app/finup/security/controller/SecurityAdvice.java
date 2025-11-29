package app.finup.security.controller;


import app.finup.common.enums.AppStatus;
import app.finup.common.exception.JwtVerifyException;
import app.finup.common.utils.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 시큐리티 로그인 예외 처리 어드바이스 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Order(1)
@Slf4j
@RestControllerAdvice(assignableTypes = {SecurityLoginController.class})
public class SecurityAdvice {

    /**
     * 아이디/비밀번호 불일치로 인한 예외 처리
     */
    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<?> handleBadCredentialsEx(BadCredentialsException e) {
        return Api.fail(AppStatus.AUTH_BAD_CREDENTIALS);
    }

    /**
     * 계정 비활성화(정지)로 인한 예외 처리
     */
    @ExceptionHandler(DisabledException.class)
    public ResponseEntity<?> handleDisabledEx(DisabledException e) {
        return Api.fail(AppStatus.AUTH_DISABLED);
    }

    /**
     * 계정 비활성화(정지)로 인한 예외 처리
     */
    @ExceptionHandler(JwtVerifyException.class)
    public ResponseEntity<?> handleJwtExpiredEx(JwtVerifyException e) {
        return Api.fail(e.getAppStatus());
    }

}
