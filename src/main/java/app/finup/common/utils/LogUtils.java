package app.finup.common.utils;

import app.finup.common.enums.LogEmoji;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.function.Supplier;

/**
 * 로깅 메세지 출력을 위한 유틸 클래스
 * @author kcw
 * @since 2025-11-27
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LogUtils {


    /**
     * 메소드를 수행하고, 메소드 완료에 소요된 시간을 측정한 로그 출력
     * @param baseMessage 출력할 기본 메세지 (이 메세지 뒤에 -시작, -완료, -실패가 붙음)
     * @param method 수행할 메소드
     */
    public static void runMethodAndShowCostLog(String baseMessage, Runnable method) {

        // 작업 시작 시간 기록
        long startTime = getStartTimeAndShowStartLog(baseMessage);

        try {
            method.run();
            showCompleteCostLog(baseMessage, startTime);
        } catch (Exception e) {
            showErrorCostLog(baseMessage, e, startTime);
            throw e; // 로그 출력 후 예외 재전파
        }
    }


    /**
     * 메소드를 수행하고, 메소드 완료에 소요된 시간을 측정한 로그 출력
     * @param baseMessage 출력할 기본 메세지 (이 메세지 뒤에 -시작, -완료, -실패가 붙음)
     * @param method 수행할 메소드
     */
    public static <T> T runMethodAndShowCostLog(String baseMessage, Supplier<T> method) {

        // 작업 시작 시간 기록
        long startTime = getStartTimeAndShowStartLog(baseMessage);

        try {
            T result = method.get();
            showCompleteCostLog(baseMessage, startTime);
            return result;

        } catch (Exception e) {
            showErrorCostLog(baseMessage, e, startTime);
            throw e; // 로그 출력 후 예외 재전파
        }
    }

    // 시작 시간 생성 및 시작 로그 출력
    private static long getStartTimeAndShowStartLog(String baseMessage) {

        // 시작 시점
        long startTime = System.currentTimeMillis();

        // 작업 시작/끝 로그 출력
        log.info("⭐ {} 시작", baseMessage);
        return startTime;
    }

    // 성공 로그 출력 (걸린 시간 계산)
    private static void showCompleteCostLog(String baseMessage, long startTime) {
        log.info("✅ {} 완료 - [소요 시간: ⌛ {}]", baseMessage, calculateCost(startTime));
    }

    // 실패 로그 출력 (걸린 시간 계산)
    private static void showErrorCostLog(String baseMessage, Exception e, long startTime) {
        log.error("❌ {} 실패 - [소요 시간: ⌛ {}]\n오류: {}", baseMessage, calculateCost(startTime), e.getMessage());
    }

    // 걸린 시간(비용, 초) 계산
    public static String calculateCost(long start) {
        double cost = (System.currentTimeMillis() - start) / 1000.0;
        return "%.4f초".formatted(cost);
    }


    /**
     * INFO 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showInfo(Class<?> clazz, String formatMessage, Object... params) {

        if (log.isInfoEnabled())
            log.info("[ {} MESSAGE ] {} : {}", clazz.getSimpleName(), getCaller(), format(formatMessage, params));

    }


    /**
     * INFO 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param startEmoji    메세지 시작에 표시할 이모지 ex. ✅
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showInfo(Class<?> clazz, LogEmoji startEmoji, String formatMessage, Object... params) {

        if (log.isInfoEnabled())
            log.info(
                    "{} [ {} ] {} : {}",
                    startEmoji.getValue(), clazz.getSimpleName(), getCaller(), format(formatMessage, params)
            );

    }


    /**
     * WARN 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showWarn(Class<?> clazz, String formatMessage, Object... params) {

        if (log.isWarnEnabled())
            log.warn("⚠️ [ {} ] {} : {}", clazz.getSimpleName(), getCaller(), format(formatMessage, params));

    }


    /**
     * ERROR 로그 출력
     * @param clazz         호출 대상 클래스 정보
     * @param formatMessage 포멧을 요구하는 메세지 ex. JWT TOKEN : %s
     * @param params        포멧 메시지에 포함될 파라미터들
     */
    public static void showError(Class<?> clazz, String formatMessage, Object... params) {

        if (log.isErrorEnabled())
            log.warn("❌ [ {} ] {} : {}", clazz.getSimpleName(), getCaller(), format(formatMessage, params));

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

