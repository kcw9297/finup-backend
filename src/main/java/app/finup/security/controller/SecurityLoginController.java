package app.finup.security.controller;

import app.finup.common.constant.Const;
import app.finup.common.constant.Url;
import app.finup.layer.domain.auth.dto.AuthDtoMapper;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import app.finup.common.enums.AppStatus;
import app.finup.common.provider.CookieProvider;
import app.finup.common.utils.Api;
import app.finup.security.jwt.provider.JwtProvider;
import app.finup.security.dto.CustomUserDetails;
import app.finup.security.dto.Login;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.time.Duration;
import java.util.Objects;

/**
 * Spring Security 기반 JWT 인증 로그인 컨트롤러 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@RestController
@RequiredArgsConstructor
public class SecurityLoginController {

    private final AuthenticationManager authenticationManager;
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @Value("${jwt.cookie-name}")
    private String jwtCookieName;

    @Value("${jwt.expiration.cookie}")
    private Duration jwtCookieExpiration;

    /**
     * Spring Security Login 처리
     * [POST] /login
     * @param rq 사용자 로그인 요청 DTO
     */
    @PostMapping(Url.LOGIN)
    public ResponseEntity<?> login(HttpServletResponse response,
                                   @RequestBody Login rq) {

        // [1] 입력 정보 기반, 인증 요청 토큰 생성
        UsernamePasswordAuthenticationToken token =
                new UsernamePasswordAuthenticationToken(rq.getEmail(), rq.getPassword());

        // [2] 인증 수행
        // 인증 실패 시, 예외가 발생하여 더 이상 진행하지 않음
        Authentication auth = authenticationManager.authenticate(token);

        // [3] 인증 성공 시, 생성된 CustomUserDetails 조회
        CustomUserDetails userDetails = (CustomUserDetails) auth.getPrincipal();
        log.warn(userDetails.toString());

        // [4] JWT 인증토큰 생성 (AT, RT 모두 발급 후, AT만 반환)
        String jwt = jwtProvider.login(userDetails);

        // [5] AT 정보를 쿠키에 담아 전달 후, 성공 응답 전달
        cookieProvider.createCookie(response, jwtCookieName, jwt, jwtCookieExpiration);
        return Api.ok(AppStatus.AUTH_OK_LOGIN, AuthDtoMapper.toLoginMember(userDetails));
    }


    /**
     * 로그아웃
     * [POST] /logout
     */
    @PostMapping(Url.LOGOUT)
    public ResponseEntity<?> logout(HttpServletRequest request, HttpServletResponse response) {

        // [1] 쿠키 내 AT 조회
        String at = cookieProvider.getValue(request, jwtCookieName);

        // [2] 만약, AT가 존재하고 만료된 상태가 아니라면 토큰 무효화 수행
        if (Objects.nonNull(at) && Objects.isNull(request.getAttribute(AppStatus.TOKEN_EXPIRED.name()))) {
            jwtProvider.logout(at); // Redis 내 RT 제거
            cookieProvider.invalidateCookie(response, jwtCookieName); // AT Cookie 무효화
            cookieProvider.invalidateCookie(response, Const.XSRF_TOKEN); // XSRF 토큰 무효화
        }

        // [3] 성공 응답 반환
        return Api.ok(AppStatus.AUTH_OK_LOGOUT);
    }


}

