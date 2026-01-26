package app.finup.security.dto;

import lombok.*;

@Getter
@ToString
@EqualsAndHashCode
@AllArgsConstructor
@Builder
public class JwtClaims {

    private String jti;
    private Long memberId;
}
