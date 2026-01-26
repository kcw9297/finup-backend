package app.finup.security.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 회원 Cache 정보를 저장하기 위한 Key를 관리하는 상수 클래스
 * @author kcw
 * @since 2026-01-26
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class SecurityRedisKey {

    // Base Key
    private static final String KEY_PREFIX = "SECURITY:";
    private static final String CACHE_PREFIX = "CACHE:" + KEY_PREFIX;
    private static final String LOCK_PREFIX = "LOCK:" + KEY_PREFIX;

    // Cache Name
    public static final String CACHE_LOGIN_MEMBER = CACHE_PREFIX + "LOGIN_MEMBER";

}
