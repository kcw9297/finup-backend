package app.finup.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

/**
 * 로깅 메세지 출력을 위한 유틸 클래스
 * @author kcw
 * @since 2025-11-27
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogUtils {

    /**
     * INFO 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showInfo(Class<?> clazz, String formatMessage, Object... params) {

        if (log.isInfoEnabled())
            log.info("[★ {} MESSAGE ★] {} : {}", clazz.getSimpleName(), getCaller(), format(formatMessage, params));

    }


    /**
     * WARN 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showWarn(Class<?> clazz, String formatMessage, Object... params) {

        if (log.isWarnEnabled())
            log.warn("[★ {} WARN ★] {} : {}", clazz.getSimpleName(), getCaller(), format(formatMessage, params));

    }


    /**
     * ERROR 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showError(Class<?> clazz, String formatMessage, Object... params) {

        if (log.isErrorEnabled())
            log.warn("[★ {} ERROR ★] {} : {}", clazz.getSimpleName(), getCaller(), format(formatMessage, params));

    }


    // 메세지 포메팅
    private static String format(String formatMessage, Object... params) {
        return params.length > 0 ? String.format(formatMessage, params) : formatMessage;
    }


    // 메소드 호출자 정보 추출
    private static String getCaller() {

        return StackWalker.getInstance(StackWalker.Option.RETAIN_CLASS_REFERENCE)
                .walk(frames ->
                        frames.skip(2).findFirst().map(StackWalker.StackFrame::getMethodName).orElse("UNKNOWN")
                );
    }
}
