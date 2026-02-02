package app.finup.common.provider;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.time.Duration;

/**
 * 쿠키 관리 기능 제공 Provider 인터페이스
 * @author kcw
 * @since 2025-11-29
 */

public interface CookieProvider {

    /**
     * 특정 쿠키 내 값 조회
     * @param request 서블릿 요청 객체
     * @param cookieName 찾기 대상 쿠키명
     * @return 조회된 쿠키 값
     */
    String getValue(HttpServletRequest request, String cookieName);

    /**
     * 쿠키 설정
     * @param response    서블릿 응답 객체
     * @param cookieName  쿠키명
     * @param cookieValue 쿠키값
     * @param expires     만료 시간 (Duration)
     */
    void createCookie(HttpServletResponse response,
                      String cookieName, String cookieValue, Duration expires);

    /**
     * 쿠키 만료 처리
     * @param response   서블릿 응답 객체
     * @param cookieName 쿠키명
     */
    void invalidateCookie(HttpServletResponse response, String cookieName);
}
