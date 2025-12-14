package app.finup.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.text.NumberFormat;
import java.time.Duration;
import java.util.Locale;
import java.util.Objects;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FormatUtils {


    /**
     * 한국식 번호 문자열로 포메팅 (0,000)
     * @param value 문자열 변환 대상 숫자
     * @return 포메팅 처리된 문자열
     */

    public static String formatKoreaNumber(Number value) {
        return NumberFormat.getNumberInstance(Locale.KOREA).format(value);
    }

    /**
     * Duration 시간, 분, 초 포메팅
     * @param duration 대상 시간 객체
     * @return 포메팅 처리된 문자열
     */

    public static String formatDuration(Duration duration) {

        // [1] 시간 정보 추출
        long hours = duration.toHours();
        long minutes = duration.toMinutesPart();
        long seconds = duration.toSecondsPart();

        // [2] "시간" 정보 유무에 따라 포멧 반환 (0분이여도, 00:01 식으로 표시)
        return hours > 0 ?
                String.format("%d:%02d:%02d", hours, minutes, seconds) : // "시" 정보가 있는 경우 "1:10:23"
                String.format("%d:%02d", minutes, seconds); // "시" 정보가 없는 경우 "15:24"
    }

    /**
     * Duration 시간, 분, 초 포메팅
     * @param secondDuration "초"로 저장된 문자열
     * @return 포메팅 처리된 문자열
     */

    public static String formatDuration(String secondDuration) {

        // [1] 유효하지 않은 시간 값이면 0:00 반환
        if (Objects.isNull(secondDuration) || Objects.equals(secondDuration, "0")) return "0:00";

        // [2] 시간 정보 추출
        long seconds = Long.parseLong(secondDuration);
        long hours = seconds / 3600;
        long minutes = (seconds % 3600) / 60;
        long secs = seconds % 60;

        // [2] "시간" 정보 유무에 따라 포멧 반환 (0분이여도, 00:01 식으로 표시)
        return hours > 0 ?
                String.format("%d:%02d:%02d", hours, minutes, secs) : // "시" 정보가 있는 경우 "1:10:23"
                String.format("%d:%02d", minutes, secs); // "시" 정보가 없는 경우 "15:24"
    }
}
