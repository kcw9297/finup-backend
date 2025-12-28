package app.finup.common.enums;

import lombok.Getter;

/**
 * 애플리케이션 상태 및 응답 메세지를 관리하는 상수 클래스
 * @author kcw
 * @since 2025-11-26
 */
@Getter
public enum AppStatus {

    /* 검증 및 인증 상태 */
    VALIDATION_INVALID_PARAMETER(400, "입력하신 값을 다시 확인해 주세요.", "VALIDATION_INVALID_PARAMETER"),
    AUTH_MAIL_NOT_FOUND(400, "탈퇴 혹은 정지 상태거나 존재하지 않는 이메일입니다.", "AUTH_MAIL_NOT_FOUND"),
    AUTH_INCORRECT_CODE(400, "인증 코드가 일치하지 않습니다.", "AUTH_INCORRECT_CODE"),
    AUTH_DUPLICATE_EMAIL(400, "이미 가입한 이메일이 존재합니다.", "AUTH_DUPLICATE_EMAIL"),
    AUTH_REQUEST_EXPIRED(403, "인증 요청이 만료되었습니다. 다시 시도해 주세요.", "AUTH_REQUEST_EXPIRED"),
    AUTH_REQUEST_ALREADY_EXIST(403, "인증 요청이 이미 존재합니다. 잠시 후에 다시 시도해 주세요.", "AUTH_REQUEST_ALREADY_EXIST"),
    AUTH_INVALID_REQUEST(403, "이미 만료되었거나 유효하지 않은 요청입니다.", "AUTH_INVALID_REQUEST"),
    AUTH_DISABLED(403, "정지된 회원입니다.", "AUTH_DISABLED"),
    AUTH_BAD_CREDENTIALS(400, "이메일과 비밀번호가 일치하지 않습니다.", "AUTH_BAD_CREDENTIALS"),
    AUTH_OK_LOGIN(200, "성공적으로 로그인 되었습니다.", "AUTH_OK_LOGIN"),
    AUTH_OK_LOGOUT(200, "성공적으로 로그아웃 되었습니다.", "AUTH_OK_LOGOUT"),
    AUTH_OK_ISSUE_CSRF(200, "CSRF 토큰을 발급받았습니다.", "AUTH_OK_ISSUE_CSRF"),
    AUTH_OAUTH2_LOGIN_FAILED(500, "소셜 로그인에 실패했습니다. 잠시 후에 다시 시도해 주세요.", "AUTH_OAUTH2_LOGIN_FAILED"),

    /* JWT 인증 정보 */
    TOKEN_NOT_FOUND(401, "로그인이 필요한 서비스입니다.", "TOKEN_NOT_FOUND"),
    TOKEN_INVALID(403, "유효하지 않은 요청입니다. 다시 로그인해 주세요.", "TOKEN_INVALID"),
    TOKEN_EXPIRED(403, "로그인이 만료되었습니다. 다시 로그인해 주세요.", "TOKEN_EXPIRED"),
    TOKEN_EXPIRED_RT(403, "RefreshToken 만료", "TOKEN_EXPIRED_RT"),
    TOKEN_REISSUE_FAILED(403, "잘못된 요청입니다. 다시 로그인해 주세요.", "TOKEN_REISSUE_FAILED"),

    /* 범용 유틸 클래스 상태 */
    UTILS_LOGIC_FAILED(500, "처리 중 오류가 발생했습니다. 잠시 후에 다시 시도해 주세요.", "UTILS_LOGIC_FAILED"),
    UTILS_REORDER_FAILED(500, "위치를 변경할 수 없습니다. 잠시 후에 다시 시도해 주세요.", "UTILS_REORDER_FAILED"),

    /* Infra - File */
    FILE_NOT_EXIST(500, "파일이 업로드되지 않았습니다. 잠시 후에 다시 시도해 주세요.", "FILE_NOT_EXIST"),
    FILE_EMPTY(500, "잘못된 업로드 요청입니다. 올바른 파일로 다시 시도해 주세요.", "FILE_EMPTY"),
    FILE_UPLOAD_FAILED(500, "파일 업로드에 실패했습니다.", "FILE_UPLOAD_FAILED"),
    FILE_REMOVE_FAILED(500, "파일 삭제에 실패했습니다.", "FILE_REMOVE_FAILED"),
    FILE_DOWNLOAD_FAILED(500, "파일 다운로드에 실패했습니다.", "FILE_DOWNLOAD_FAILED"),

    /* Infra - YouTube */
    YOUTUBE_URL_NOT_VALID(400, "유효하지 않은 YouTube URL 입니다. 다시 시도해 주세요.", "YOUTUBE_URL_NOT_VALID"),
    YOUTUBE_ID_NOT_VALID(400, "유효하지 않은 YouTube 번호 입니다. 다시 시도해 주세요.", "YOUTUBE_ID_NOT_VALID"),
    YOUTUBE_VIDEO_NOT_FOUND(500, "삭제되었거나 숨김 처리 된 영상입니다.", "YOUTUBE_ID_NOT_VALID"),
    YOUTUBE_REQUEST_FAILED(500, "유튜브 영상 조회에 실패했습니다.", "YOUTUBE_REQUEST_FAILED"),

    /* Infra - AI */
    AI_CHAT_RESPONSE_ERROR(500, "AI 분석에 실패했습니다. 잠시 후 다시 시도해 주세요.", "AI_CHAT_RESPONSE_ERROR"),


    /* ======================================== 작성 영역 (외에는 건들이지 말 것) ====================================== */

    /* 예시 - Reboard */
    REBOARD_NOT_FOUND(400, "존재하지 않거나 이미 삭제된 게시글입니다.", "REBOARD_NOT_FOUND"),
    REBOARD_OK_WRITE(200, "게시글을 작성했습니다.", "REBOARD_OK_WRITE"),
    REBOARD_OK_EDIT(200, "게시글을 수정했습니다.", "REBOARD_OK_EDIT"),

    /* 파일 UploadFile */
    UPLOAD_FILE_NOT_FOUND(500, "파일이 존재하지 않습니다.", "UPLOAD_FILE_NOT_FOUND"),
    UPLOAD_FILE_ADD(200, "파일 업로드에 성공했습니다.", "UPLOAD_FILE_ADD"),
    /* 회원 Member */
    MEMBER_NOT_FOUND(400, "회원 정보가 존재하지 않습니다.", "MEMBER_NOT_FOUND"),
    MEMBER_DUPLICATE_NICKNAME(400, "이미 사용 중인 닉네임입니다.", "MEMBER_DUPLICATE_NICKNAME"),
    /* 단계 학습 Study */
    STUDY_NOT_FOUND(400, "존재하지 않거나 이미 삭제된 학습 정보입니다.", "STUDY_NOT_FOUND"),
    STUDY_NOT_UPDATABLE(400, "수정된 정보가 없습니다.", "STUDY_NOT_UPDATABLE"),
    STUDY_OK_ADD(200, "단계학습 정보를 추가했습니다.", "STUDY_OK_ADD"),
    STUDY_OK_EDIT(200, "단계학습 정보를 수정했습니다.", "STUDY_OK_EDIT"),
    STUDY_OK_REMOVE(200, "단계학습 정보를 삭제했습니다.", "STUDY_OK_REMOVE"),

    /* 학습 단어 StudyWord */
    STUDY_WORD_NOT_FOUND(400, "단어 목록이 갱신되었습니다. 새로고침 후 다시 시도해주세요.", "STUDY_WORD_NOT_FOUND"),
    STUDY_WORD_ALREADY_EXIST(400, "이미 같은 단어가 존재합니다.", "STUDY_WORD_ALREADY_EXIST"),
    STUDY_WORD_IMAGE_NOT_FOUND(400, "등록된 이미지가 없습니다.", "STUDY_WORD_IMAGE_NOT_FOUND"),
    STUDY_WORD_OK_ADD(200, "학습 단어를 추가했습니다.", "STUDY_WORD_OK_ADD"),
    STUDY_WORD_OK_UPLOAD_IMAGE(200, "학습 단어 이미지를 등록했습니다.", "STUDY_WORD_OK_UPLOAD_IMAGE"),
    STUDY_WORD_OK_EDIT(200, "학습 단어를 수정했습니다.", "STUDY_WORD_OK_EDIT"),
    STUDY_WORD_OK_REMOVE(200, "학습 단어를 삭제했습니다.", "STUDY_WORD_OK_REMOVE"),
    STUDY_WORD_OK_REMOVE_IMAGE(200, "학습 단어를 이미지를 삭제했습니다.", "STUDY_WORD_OK_REMOVE_IMAGE"),
    WORDS_NOT_FOUND(400,"해당 단어를 찾을 수 없습니다.", "WORDS_NOT_FOUND"),

    /* 내 단어장 MemberWordbook */
    WORD_NOT_FOUND(400, "존재하지 않는 단어입니다.", "WORD_NOT_FOUND"),
    MEMBER_WORDBOOK_ALREADY_EXISTS(400, "이미 존재하는 단어입니다.", "MEMBER_WORDBOOK_ALREADY_EXISTS"),
    WORD_MEMORIZE_OK(200, "단어 암기를 완료했습니다.", "WORD_MEMORIZE_OK"),


    /* 영상 정보 VideoLink */
    VIDEO_LINK_NOT_FOUND(500, "영상 목록이 갱신되었습니다. 새로고침 후 다시 시도해 주세요.", "VIDEO_LINK_NOT_FOUND"),
    VIDEO_LINK_ALREADY_EXISTS(400, "이미 존재하는 영상입니다.", "VIDEO_LINK_ALREADY_EXISTS"),
    VIDEO_LINK_OK_ADD(200, "학습 영상을 추가했습니다.", "VIDEO_LINK_OK_ADD"),
    VIDEO_LINK_OK_EDIT(200, "학습 영상 링크를 수정했습니다.", "VIDEO_LINK_OK_EDIT"),
    VIDEO_LINK_OK_REMOVE(200, "학습 영상을 삭제했습니다.", "VIDEO_LINK_OK_REMOVE"),


    /* 공지사항 Notice */
    NOTICE_NOT_FOUND(400, "존재하지 않는 공지사항 게시글입니다.", "NOTICE_NOT_FOUND"),
    NOTICE_OK_WRITE(200, "공지사항을 작성했습니다.", "NOTICE_OK_WRITE"),
    NOTICE_OK_EDIT(200, "공지사항을 수정했습니다.", "NOTICE_OK_EDIT"),
    NOTICE_OK_REMOVE(200, "공지사항을 삭제했습니다.", "NOTICE_OK_REMOVE"),


    /* 금융 사전 API */
    FINANCE_DICT_API_FAILED(400, "KSD 금융용어 API 호출을 실패하였습니다.", "FINANCE_DICT_API_FAILED"),
    FINANCE_DICT_ALREADY_INITIALIZED(400, "금융 용어 사전은 이미 초기화되었습니다.", "FINANCE_DICT_ALREADY_INITIALIZED"),

    /* 북마크 */
    BOOKMARK_ALREADY_EXISTS(400, "이미 북마크한 항목입니다.", "BOOKMARK_ALREADY_EXISTS"),

    /* ======================================== 작성 영역 끝 ========================================================= */

    /* 범용 상태 */
    OK(200, "요청에 성공했습니다.", "OK"),
    SERVER_ERROR(500, "서버 오류가 발생했습니다. 잠시 후에 다시 시도해 주세요.", "SERVER_ERROR"),
    METHOD_NOT_ALLOWED(405, "지원하지 않는 형식입니다.", "METHOD_NOT_ALLOWED"),
    CACHE_EXPIRED(403, "정보가 만료되었습니다.", "CACHE_EXPIRED"),
    UNAUTHORIZED(401, "로그인이 필요한 서비스입니다.", "UNAUTHORIZED"),
    ACCESS_DENIED(403, "잘못된 요청입니다.", "ACCESS_DENIED");


    private final int httpCode;     // HTTP 코드
    private final String message;   // 전달 메세지
    private final String status;    // 결과 상태를 표현하는 문자열

    AppStatus(int httpCode, String message, String status) {
        this.httpCode = httpCode;
        this.message = message;
        this.status = status;
    }

    /**
     * AppStatus 메세지 출력
     * @return AppStatus 상수 값들을 모두 제공하는 문자열
     */
    public String getInfo() {
        return "[APP STATUS] status : %s, httpCode: %d, message: %s".formatted(status, httpCode, message);
    }
}
