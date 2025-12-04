package app.finup.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class Url {

    /* BASIC */
    public static final String API = "/api";
    public static final String OAUTH = "/oauth";
    public static final String PUBLIC = "/public";
    public static final String ADMIN = "/admin";

    /* LOGIN, LOGOUT */
    public static final String LOGIN = API + "/login";
    public static final String LOGOUT = API + "/logout";

    /* BASIC - PATTERN */
    public static final String PATTERN_API = API + "/**";
    public static final String PATTERN_OAUTH = OAUTH + "/**";
    public static final String PATTERN_PUBLIC = PUBLIC + "/**";
    public static final String PATTERN_ADMIN = ADMIN + "/**";

    /* ======================================== 작성 영역 (외에는 건들이지 말 것) ====================================== */

    /* auth */
    public static final String AUTH = API + "/auth";

    /* reboard */
    public static final String REBOARD = API + "/reboards";
    public static final String REBOARD_PUBLIC = PUBLIC + REBOARD;
    public static final String REBOARD_ADMIN = ADMIN + REBOARD;

    /* member */
    public static final String MEMBER = API + "/members";
    public static final String MEMBER_ADMIN = ADMIN + MEMBER;
    public static final String MEMBER_PUBLIC = PUBLIC + MEMBER;

    /* news */
    public static final String NEWS = API + "/news";
    public static final String NEWS_PUBLIC = PUBLIC + NEWS;

    /* stocks */
    public static final String STOCKS = API + "/stocks";
    public static final String STOCKS_PUBLIC = PUBLIC + STOCKS;

    /* admin */
    public static final String ADMIN_API = API + ADMIN;

    /* notice */
    public static final String NOTICE = API + "/notices";
    public static final String NOTICE_ADMIN_API = ADMIN + NOTICE;

    /* ======================================== 작성 영역 끝 ========================================================= */

    /* DOMAIN - PATTERN */
    public static final String PATTERN_AUTH = AUTH + "/**";

}
