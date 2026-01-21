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
    public static final String PATTERN_AUTH = AUTH + "/**";

    /* uploadFile */
    public static final String UPLOAD_FILE = API + "/upload-file";
    public static final String UPLOAD_FILE_PUBLIC = PUBLIC + UPLOAD_FILE;
    public static final String UPLOAD_FILE_ADMIN = ADMIN + UPLOAD_FILE;

    /* reboard */
    public static final String REBOARD = API + "/reboards";
    public static final String REBOARD_PUBLIC = PUBLIC + REBOARD;
    public static final String REBOARD_ADMIN = ADMIN + REBOARD;

    /* member */
    public static final String MEMBER = API + "/members";
    public static final String MEMBER_ADMIN = ADMIN + MEMBER;
    public static final String MEMBER_PUBLIC = PUBLIC + MEMBER;

    /* study */
    public static final String STUDY = API + "/studies";
    public static final String STUDY_ADMIN = ADMIN + STUDY;
    public static final String STUDY_PUBLIC = PUBLIC + STUDY;

    /* studyProgress */
    public static final String STUDY_PROGRESS = API + "/study-progresses";
    public static final String STUDY_PROGRESS_ADMIN = ADMIN + STUDY_PROGRESS;
    public static final String STUDY_PROGRESS_PUBLIC = PUBLIC + STUDY_PROGRESS;

    /* studyWord */
    public static final String STUDY_WORD = API + "/study-words";
    public static final String STUDY_WORD_ADMIN = ADMIN + STUDY_WORD;
    public static final String STUDY_WORD_PUBLIC = PUBLIC + STUDY_WORD;

    /* video */
    public static final String VIDEO = API + "/videos";
    public static final String VIDEO_ADMIN = ADMIN + VIDEO;
    public static final String VIDEO_PUBLIC = PUBLIC + VIDEO;

    /* videoLink */
    public static final String VIDEO_LINK = API + "/video-links";
    public static final String VIDEO_LINK_ADMIN = ADMIN + VIDEO_LINK;
    public static final String VIDEO_LINK_PUBLIC = PUBLIC + VIDEO_LINK;

    /* bookmark */
    public static final String BOOKMARK = API + "/bookmarks";
    public static final String BOOKMARK_ADMIN = ADMIN + BOOKMARK;
    public static final String BOOKMARK_PUBLIC = PUBLIC + BOOKMARK;

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
    public static final String NOTICE_ADMIN = ADMIN + NOTICE;
    public static final String NOTICE_PUBLIC = PUBLIC + NOTICE;

    /* level */
    public static final String LEVEL = API + "/levels";
    public static final String LEVEL_ADMIN = ADMIN + LEVEL;
    public static final String LEVEL_PUBLIC = PUBLIC + LEVEL;

    /* words */
    public static final String WORDS = API + "/words";
    public static final String ADMIN_WORDS = ADMIN + WORDS;
    public static final String WORDS_PUBLIC = PUBLIC + WORDS;

    /* memberWordbook */
    public static final String MEMBER_WORDBOOK = MEMBER + "/wordbook";
    public static final String MEMBER_WORD_VIEW = MEMBER_WORDBOOK + "/view";

    /* word quiz */
    public static final String WORD_QUIZ = API + "/word-quizzes";

    /* indicator */
    public static final String INDICATOR = API + "/indicators";
    public static final String INDICATOR_PUBLIC = PUBLIC + INDICATOR;

    /* quiz */
    public static final String QUIZ = API + "/quiz";

    /* ======================================== 작성 영역 끝 ========================================================= */

}
