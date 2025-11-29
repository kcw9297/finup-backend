package app.finup.layer.domain.auth.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.common.utils.LogUtils;
import app.finup.layer.domain.auth.dto.AuthDtoMapper;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(Url.AUTH)
@RequiredArgsConstructor
public class AuthController {

    /**`
     * 로그인 상태 확인
     * [GET] /auth/me
     * @param userDetails 인증에 성공한 경우 존재하는 Principal
     */
    @GetMapping("/me")
    public ResponseEntity<?> getMe(@AuthenticationPrincipal CustomUserDetails userDetails) {

        // [1] 로그 출력
        LogUtils.showWarn(this.getClass(), "현재 회원의 userDetails = %s", userDetails);

        // [2] UserDetails 존재 시 회원 정보를 담은 DTO 전달
        return Objects.isNull(userDetails) ?
                Api.ok() :
                Api.ok(AuthDtoMapper.toLoginMember(userDetails));
    }

    /**
     * CSRF 토큰 발급 (GET 외 요청 시, Spring Security 자동 발급)
     * [GET] /auth/csrf
     */
    @GetMapping("/csrf")
    public ResponseEntity<?> getCsrf() {
        return Api.ok(AppStatus.AUTH_OK_ISSUE_CSRF);
    }
}
