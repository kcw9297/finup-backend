package app.finup.layer.domain.auth.dto;

import app.finup.security.dto.CustomUserDetails;
import lombok.*;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AuthDtoMapper {

    public static AuthDto.LoginMember toLoginMember(CustomUserDetails userDetails) {

        return AuthDto.LoginMember.builder()
                .memberId(userDetails.getMemberId())
                .nickname(userDetails.getNickname())
                .email(userDetails.getEmail())
                .role(userDetails.getRole())
                .social(userDetails.getSocial())
                .profileImageUrl(userDetails.getProfileImageUrl())
                .build();
    }
}
