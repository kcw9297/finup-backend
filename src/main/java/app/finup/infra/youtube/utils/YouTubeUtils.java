package app.finup.infra.youtube.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * YouTube URL 내 필요 정보를 파싱하기 위한 유틸 클래스
 * @author kcw
 * @since 2025-12-05
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YouTubeUtils {

    /*
        유튜브 영상은 아래처럼 다양한 형태의 URL로 접근 가능함
        하지만 고유번호(videoId) 는 11자리로 항상 동일함
        1. https://www.youtube.com/watch?v=dFcDgrYuA9A
        2. https://www.youtube.com/watch?v=dFcDgrYuA9A&t=379s
        3. https://youtu.be/dFcDgrYuA9A
        4. https://youtu.be/dFcDgrYuA9A?si=EFz5teqZUaoOmD0z
        5. https://m.youtube.com/watch?v=dFcDgrYuA9A
        6. https://www.youtube.com/embed/dFcDgrYuA9A

        그룹(...) 패턴 사용 시, 내부에 마치 OR 처럼 ("|") 패턴을 비교할 수 있음
        패턴 매칭 결과를 "group" 이용하여 추출 가능.
        (?:...) 는 비캡처 그룹으로, 매칭 여부에만 사용하고 실제 그룹에 포함되지 않음
        (...)는 캡처 그룹으로, 실제 그룹에 저장되는 값
     */
    public static final String PATTERN_VIDEO_ID =
            "(?:youtube\\.com/watch\\?v=|youtu\\.be/|youtube\\.com/embed/)([a-zA-Z0-9_-]{11})";

    public static final String PATTERN_VIDEO_URL =
            "^(https?://)?(www\\.|m\\.)?(youtube\\.com/(watch\\?v=|embed/)|youtu\\.be/)[a-zA-Z0-9_-]{11}.*$";


    /**
     * URL 내 videoId 추출
     * @param videoUrl 유튜브 full url 주소 (원본 그대로)
     * @return 파싱된 videoId
     */
    public static String parseVideoId(String videoUrl) {

        // [1] URL 매핑 비교 (패턴 매핑은 Pattern.compile 사용 필요)
        Matcher matcher = Pattern.compile(PATTERN_VIDEO_ID).matcher(videoUrl);

        // [2] 패턴에 매핑된 경우, 매칭된 첫 번째 캡처 그룹 반환
        // (?:...) 는 미포함되니, 가장 먼저 (...)를 사용한 11자리 ID 값
        if (matcher.find()) return matcher.group(1);

        // [3] 아무것도 매칭하지 못한 경우 예외 반환 (잘못된 URL 요청)
        throw new UtilsException(AppStatus.YOUTUBE_URL_NOT_VALID);
    }


    /**
     * YouTube 영상 URL 으로 변경 (videoId 기반)
     * @param videoId 유튜브 영상 비디오 고유번호
     * @return 영상 URL
     */
    public static String toVideoUrl(String videoId) {
        return "https://www.youtube.com/watch?v=%s".formatted(videoId);
    }


    /**
     * YouTube API용 날짜 형식 변환
     * @param dateTime 변환할 날짜시간
     * @return RFC 3339 형식 문자열 (예: "2024-12-06T00:00:00Z")
     */
    public static String toYouTubeDateTime(LocalDateTime dateTime) {

        return dateTime
                .atZone(ZoneId.of("UTC"))
                .format(DateTimeFormatter.ISO_INSTANT);
    }

}
