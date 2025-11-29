package app.finup.layer.domain.auth.dto;

import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthDto {

    /**
     * 클라이언트에 저공할 로그인 회원 정보
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class LoginMember {

        private Long memberId;
        private String email;
        private String nickname;
        private String role;
        private String social;
        private String profileImageUrl;
    }

}
