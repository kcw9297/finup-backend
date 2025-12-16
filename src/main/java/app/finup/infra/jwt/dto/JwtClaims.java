package app.finup.infra.jwt.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class JwtClaims {

    private String jti;
    private Long memberId;
    private String email;
    private String nickname;
    private String role;
    private String social;
    private String profileImageUrl;
}
