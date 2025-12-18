package app.finup.security.dto;

import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.member.enums.MemberSocial;
import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * OAuth2 소셜 로그인 제공자로부터 정보를 임시 보관할 DTO
 * @author kcw
 * @since 2025-12-18
 */
@Data
@Builder
@AllArgsConstructor(access = AccessLevel.PRIVATE)
public class OAuth2UserInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    private String nickname;
    private String email; // 사용자가 얼마든 바꿀 수 있는 값 (UK 사용 금지)
    private MemberSocial social;
    private String providerId;
}
