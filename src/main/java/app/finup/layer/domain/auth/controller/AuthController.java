package app.finup.layer.domain.auth.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.common.utils.LogUtils;
import app.finup.layer.domain.auth.dto.AuthDtoMapper;
import app.finup.layer.domain.auth.service.AuthService;
import app.finup.security.dto.CustomUserDetails;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.web.csrf.CsrfToken;
import org.springframework.web.bind.annotation.*;



import java.util.Objects;

@Slf4j
@RestController
@RequestMapping(Url.AUTH)
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

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
    @PostMapping("/csrf")
    public ResponseEntity<?> getCsrf() {
        return Api.ok(AppStatus.AUTH_OK_ISSUE_CSRF);
    }
    /**
     * 회원가입 이메일 인증코드 발송
     * [POST] /api/auth/join/email
     * body: { "email": "test@example.com" }
     */
    @PostMapping("/join/email")
    public ResponseEntity<?> sendJoinEmail(@RequestBody JoinEmailRequest request) {

        String email = request.getEmail();
        LogUtils.showInfo(this.getClass(), "회원가입 인증코드 발송 요청 = %s", email);

        authService.sendJoinEmail(email);

        // AppStatus 안 써도 되면 그냥 OK만
        return Api.ok();
    }

    /**
     * 회원가입 이메일 인증코드 검증
     * [POST] /api/auth/join/email/verify
     * body: { "email": "test@example.com", "code": "123456" }
     */
    @PostMapping("/join/email/verify")
    public ResponseEntity<?> verifyJoinEmail(@RequestBody JoinEmailVerifyRequest request) {

        String email = request.getEmail();
        String code  = request.getCode();

        LogUtils.showInfo(this.getClass(),
                "회원가입 이메일 코드 검증 요청 email=%s code=%s", email, code);

        authService.verifyJoinEmail(email, code);

        return Api.ok();
    }

    /* ====== 요청 DTO ====== */

    @Data
    public static class JoinEmailRequest {
        private String email;
    }

    @Data
    public static class JoinEmailVerifyRequest {
        private String email;
        private String code;
    }
}