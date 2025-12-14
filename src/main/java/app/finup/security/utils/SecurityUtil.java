package app.finup.security.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.security.dto.CustomUserDetails;
import lombok.NoArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

/**
 * Spring Security 인증 사용자 유틸
 * @author khj
 * @since 2025-12-14
 */

@NoArgsConstructor
public final class SecurityUtil {

    /**
     * 로그인한 회원 ID 조회
     */

    public static Long getLoginMemberId() {

        Authentication authentication =
                SecurityContextHolder.getContext().getAuthentication();

        if (authentication == null || !authentication.isAuthenticated()) {
            throw new BusinessException(AppStatus.TOKEN_NOT_FOUND);
        }

        Object principal = authentication.getPrincipal();

        // Case 1: CustomUserDetails 사용 중
        if (principal instanceof CustomUserDetails user) {
            return user.getMemberId();
        }

        // Case 2: memberId를 String으로 넣은 경우
        if (principal instanceof String memberId) {
            return Long.valueOf(memberId);
        }

        throw new BusinessException(AppStatus.TOKEN_NOT_FOUND);
    }
}
