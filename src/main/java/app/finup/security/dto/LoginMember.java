package app.finup.security.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;

/**
 * 로그인 회원 정보를 저장할 DTO
 * @author kcw
 * @since 2025-11-26
 */

@Data
@Builder
@AllArgsConstructor
public class LoginMember implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private Long memberId;
    private String email;
    private String nickname;
    private String role;
    private String social;
    private String profileImageUrl;

}
