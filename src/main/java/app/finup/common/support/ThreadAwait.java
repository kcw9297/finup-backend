package app.finup.common.support;

import java.time.Duration;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicLong;

/**
 * 병렬 처리중인 쓰레드를 일괄 대기처리를 지원하는 클래스
 * @author kcw
 * @since 2026-01-28
 */
public class ThreadAwait {

    // 대기 시간
    private final AtomicLong waitTime = new AtomicLong(0);

    /**
     * 대기 시간 설정
     * @param waitTime 대기 시간
     */
    public void setWaitTime(Duration waitTime) {
        setTime(waitTime.toMillis());
    }


    /**
     * 대기 시간 설정
     * @param waitTime 대기 시간 (밀리초)
     */
    public void setWaitTimeMillis(long waitTime) {
        setTime(waitTime);
    }

    // 설정 수행
    private void setTime(long time) {
        if (time <= waitTime.get()) return; // 만약 다른 요청이 간섭하는 경우 방지
        waitTime.set(time);
    }


    /**
     * 일괄 pause 처리 후, staggered jitter 적용
     * @param maxWait 최대 대기 시간
     * @param baseJitter 기본 jitter 범위
     * @throws InterruptedException 인터럽트 발생 시
     */
    public void awaitIfPaused(Duration maxWait, Duration baseJitter) throws InterruptedException {

        // [1] 현재 대기시간 조회 (없는 경우 통과)
        long wait = waitTime.get();
        if (wait <= 0) return;

        // [2] wait 제한
        long baseJitterMillis = baseJitter.toMillis();
        wait = Math.min(wait, maxWait.toMillis());

        // [3] jitter 계산 후 최종 대기시간 추가
        long jitter = ThreadLocalRandom.current().nextLong(0, baseJitterMillis + 1);
        long totalWait = Math.max(wait + jitter, 0);

        // [4] 대기 수행
        TimeUnit.MILLISECONDS.sleep(totalWait);
    }

    /**
     * pause 해제
     */
    public void clear() {
        waitTime.set(0);
    }

}

