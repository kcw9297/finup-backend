package app.finup.infra.redisson.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 분산 MultiLock 어노테이션
 * @author kcw
 * @since 2026-01-05
 */
@Target(ElementType.METHOD)
@Retention(RetentionPolicy.RUNTIME)
public @interface RedissonMultiLock {

    /**
     * Lock 식별 Keys (복수개)
     */
    String[] keys();

    /**
     * Lock 대기 시간
     */
    long waitTime() default 0L;

    /**
     * 락 점유 시간
     * 이 시간이 지나면 자동으로 락 해제
     */
    long leaseTime() default 600L;

    /**
     * 시간 단위 (기본단위 : 초)
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}
