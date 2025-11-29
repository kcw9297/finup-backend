package app.finup.security.handler;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.stereotype.Component;

import java.util.Objects;

/**
 * 인증되지 않은 사용자 접근 처리 클래스
 * @author kcw
 * @since 2025-11-27
 */

@Slf4j
@Component
public class CustomAuthenticationEntryPoint implements AuthenticationEntryPoint {

    @Override
    public void commence(HttpServletRequest request, HttpServletResponse response, AuthenticationException authException) {

        // 토큰 만료로 인한 검증 실패인지 확인
        boolean isTokenExpired = Objects.nonNull(request.getAttribute(AppStatus.TOKEN_EXPIRED.name()));

        // 토큰 만료에 의한 것이면, 다시 로그인이 필요함을 알림
        if (isTokenExpired) Api.writeFail(response, AppStatus.TOKEN_EXPIRED);

        // 로그인을 하지 않은 경우, 로그인이 필요한 서비스임을 알림
        else Api.writeFail(response, AppStatus.UNAUTHORIZED);
    }
}