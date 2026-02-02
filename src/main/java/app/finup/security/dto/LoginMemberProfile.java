package app.finup.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

/**
 * 로그인 회원 정보를 저장할 DTO
 * @author kcw
 * @since 2025-11-26
 */

@Data
@Builder
@AllArgsConstructor
public class LoginMemberProfile {

    private Long memberId;
    private String email;
    private String nickname;
    private String role;
    private String social;
    private String profileImageUrl;

    public static LoginMemberProfile of(CustomUserDetails userDetails) {

        return LoginMemberProfile.builder()
                .memberId(userDetails.getMemberId())
                .email(userDetails.getEmail())
                .nickname(userDetails.getNickname())
                .role(userDetails.getRole())
                .social(userDetails.getSocial())
                .profileImageUrl(userDetails.getProfileImageUrl())
                .build();
    }
}
