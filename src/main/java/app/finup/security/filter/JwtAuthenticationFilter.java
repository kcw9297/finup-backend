package app.finup.security.filter;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.JwtVerifyException;
import app.finup.common.utils.Api;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import app.finup.common.provider.CookieProvider;
import app.finup.common.utils.LogUtils;
import app.finup.security.dto.JwtClaims;
import app.finup.security.jwt.provider.JwtProvider;
import app.finup.layer.domain.member.enums.MemberRole;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * JWT 토큰 인증을 위한 Servlet Filter
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    // 사용 의존성
    private final JwtProvider jwtProvider;
    private final CookieProvider cookieProvider;

    @Value("${jwt.cookie-name}")
    private String cookieName;

    @Value("${jwt.expiration.cookie}")
    private Duration cookieExpiration;

    /*
        [ 인증 필터 - JWT 토큰 검증 및 재발급 처리 ]
            1. 쿠키에서 Access Token 추출
            2. 토큰 검증 수행
            3. 만료 시 Refresh Token으로 재발급
            4. 인증 성공 시 SecurityContext에 저장
            5. 실패 시 에러 응답 반환 및 필터 체인 중단 (AT 쿠키 삭제 처리)
     */

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {

        try {

            // [1] Cookie 내 포함된 AT 조회
            String at = cookieProvider.getValue(request, cookieName);
            String xsrfToken = request.getHeader("X-XSRF-TOKEN");
            //LogUtils.showInfo(this.getClass(), "\nCOOKIE JWT TOKEN : %s\nHEADER XSRF-TOKEN : %s", at, xsrfToken);

            // [2] 토큰이 있는 경우 검증 수행 (위조, 만료 등..)
            if (Objects.nonNull(at) && !at.isBlank()) verifyToken(response, at);
            filterChain.doFilter(request, response);

            // 토큰 인증에 실패하거나, 재발급 실패 시 실패 응답 반환
        } catch (JwtVerifyException e) {
            LogUtils.showWarn(this.getClass(), "JWT 인증 실패 - 미인증 상태로 처리: %s", e.getAppStatus().getInfo());
            cookieProvider.invalidateCookie(response, cookieName); // 지금 인증 쿠키는 유효하지 않으니 제거
            request.setAttribute(AppStatus.TOKEN_EXPIRED.name(), true); // 토큰이 만료되었음을 안내하는 속성 추가
            filterChain.doFilter(request, response); // 미인증 상태로 그대로 통과

            // 예기치 않은 오류가 발생한 경우
        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "JWT 인증 실패 - 요청 중단 : %s", e.getMessage());
            cookieProvider.invalidateCookie(response, cookieName); // 지금 인증 쿠키는 유효하지 않으니 제거
            Api.writeFail(response, AppStatus.TOKEN_INVALID); // 필터 처리 중단 (로직 실행하지 않음)
        }
    }


    // 토큰 검증 & 인증 성공 처리 (재발급 로직도 포함되어야 함)
    private void verifyToken(HttpServletResponse response, String at) {

        // [1] JWT Claims 조회
        JwtClaims jwtClaims = getJwtClaims(response, at);

        // [2] JWT Claims 기반 UserDetails 생성
        CustomUserDetails userDetails = setUserDetails(jwtClaims);

        // [3] 인증 성공처리를 위한 객체 세팅 (인증 객체에는 특별한 추가 정보를 부여하지 않음)
        Authentication authentication =
                new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
        SecurityContextHolder.getContext().setAuthentication(authentication);
    }


    // JWT Claims 조회 시도
    private JwtClaims getJwtClaims(HttpServletResponse response, String at) {

        try {
            // [1] JWT 인증 후, 토큰 내 Claims 조회 시도
            return jwtProvider.getClaims(at);

            // 인증 예외 발생 시 처리
        } catch (JwtVerifyException e) {

            // [2] AT만료 이외의 사유인 경우 그대로 던짐 (RT 만료인 경우도 던짐)
            if (!Objects.equals(e.getAppStatus(), AppStatus.TOKEN_EXPIRED))
                throw e;

            // [3] Claims 조회 시도
            return reissueAndGetClaims(response, at);
        }
    }


    // 만료된 AT 재할당 및 Claims 조회
    private JwtClaims reissueAndGetClaims(HttpServletResponse response, String at) {

        try {
            // [1] AT 재발급
            String reissuedAt = jwtProvider.reissue(at);

            // [2] 새 쿠키 생성
            cookieProvider.createCookie(response, cookieName, reissuedAt, cookieExpiration);

            // [3] 재발급된 토큰에서 Claims 조회 및 반환
            return jwtProvider.getClaims(reissuedAt);

            // 다시 실패한 경우엔 예외 던짐
        } catch (JwtVerifyException e) {
            throw e;

            // 예상 외의 예외가 발생한 경우
        } catch (Exception e) {
            log.error("JWT 재발급 로직 실패: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_REISSUE_FAILED);
        }
    }


    // JWT Claims 정보 기반 UserDetails 생성
    private CustomUserDetails setUserDetails(JwtClaims jwtClaims) {

        return CustomUserDetails.builder()
                .memberId(jwtClaims.getMemberId())
                .email(jwtClaims.getEmail())
                .nickname(jwtClaims.getNickname())
                .role(jwtClaims.getRole())
                .social(jwtClaims.getSocial())
                .profileImageUrl(jwtClaims.getProfileImageUrl())
                .authorities(List.of(new SimpleGrantedAuthority(MemberRole.valueOf(jwtClaims.getRole()).getAuthority())))
                .build();
    }
}
