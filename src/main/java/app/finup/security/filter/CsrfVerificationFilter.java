package app.finup.security.filter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.Objects;

/**
 * CSRF 토큰 자동 생성처리 필터
 * @author kcw
 * @since 2025-11-28
 */
@Slf4j
@Component
public class CsrfVerificationFilter extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        // [1] request 내 CSRF 토큰 조회
        CsrfToken csrfToken = (CsrfToken) request.getAttribute(CsrfToken.class.getName());

        // [2] 만약 토큰 값이 없는 경우 (최초 HTTP 요청인 경우) 새롭게 생성 처리
        if (Objects.nonNull(csrfToken)) csrfToken.getToken();
        filterChain.doFilter(request, response); // 필터 진행

    }
}
