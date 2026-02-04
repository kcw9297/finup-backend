package app.finup.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Const {

    /* 일반 상수 */
    public static final String MEMBER_ID = "memberId";
    public static final String JTI = "jti";
    public static final String EMAIL = "email";
    public static final String NICKNAME = "nickname";
    public static final String IS_ACTIVE = "isActive";
    public static final String ROLE = "role";
    public static final String SOCIAL = "social";
    public static final String XSRF_TOKEN = "XSRF-TOKEN";
    public static final String ISO8601 = "ISO8601";
    public static final String ASIA_SEOUL = "Asia/Seoul";
    public static final String GLOBAL = "global";

    /* 접두사 상수 */
    public static final String PREFIX_KEY_JWT = "RT:";
    public static final String PREFIX_BEARER = "Bearer ";
}
