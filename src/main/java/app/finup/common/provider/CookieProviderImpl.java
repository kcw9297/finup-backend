package app.finup.common.provider;

import app.finup.common.utils.EnvUtils;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseCookie;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.Arrays;
import java.util.Objects;

/**
 * CookieProvider 구현 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class CookieProviderImpl implements CookieProvider {

    // 현재 환경 변수
    private final Environment env;

    @Value("${app.domain}")
    private String domain;

    @Override
    public String getValue(HttpServletRequest request, String cookieName) {

        // [1] 요청 내 쿠키 조회
        Cookie[] cookies = request.getCookies();

        // [2] 찾는 쿠키가 존재하는지 확인 후, 값 반환
        return Objects.isNull(cookies) ?
                null :
                Arrays.stream(cookies)
                        .filter(cookie -> Objects.equals(cookie.getName(), cookieName))
                        .map(Cookie::getValue)
                        .findFirst()
                        .orElse(null);
    }


    @Override
    public void createCookie(HttpServletResponse response,
                             String cookieName, String cookieValue, Duration expires) {

        ResponseCookie cookie = issue(cookieName, cookieValue, expires);
        response.setHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    @Override
    public void invalidateCookie(HttpServletResponse response, String cookieName) {
        ResponseCookie cookie = issue(cookieName, "", Duration.ZERO);
        response.addHeader(HttpHeaders.SET_COOKIE, cookie.toString());
    }


    // 쿠키 생성
    private ResponseCookie issue(String cookieName, String cookieValue, Duration expires) {

        // [1] 기본 설정대로 쿠키 builder 생성
        boolean isProd = EnvUtils.isProd(env);

        ResponseCookie.ResponseCookieBuilder cookieBuilder =
                ResponseCookie.from(cookieName, cookieValue)
                        .httpOnly(true) // js 접근 불가
                        .secure(isProd) // 배포 환경이면 true, 로컬 환경이면 false
                        .maxAge(expires) // 만료 시간
                        .path("/")
                        .sameSite(isProd ? "None" : "Lax");

        // [2] domain 정보가 존재하는 경우 삽입
        if (Objects.nonNull(domain) && !domain.isBlank()) cookieBuilder.domain(domain);

        // [3] 쿠키 생성
        return cookieBuilder.build();
    }

}
