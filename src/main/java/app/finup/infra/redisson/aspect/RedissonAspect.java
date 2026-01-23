package app.finup.infra.redisson.aspect;

import app.finup.common.enums.AppStatus;
import app.finup.common.enums.LogEmoji;
import app.finup.common.exception.LockException;
import app.finup.common.utils.LogUtils;
import app.finup.infra.redisson.annotation.RedissonLock;
import app.finup.infra.redisson.annotation.RedissonMultiLock;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.redisson.api.RLock;
import org.redisson.api.RedissonClient;
import org.redisson.client.RedisException;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.Objects;
import java.util.concurrent.TimeUnit;

/**
 * Redisson 어노테이션이 있는 메소드 적용 로직 Aspect 클래스
 * @author kcw
 * @since 2026-01-05
 */

@Slf4j
@Aspect
@Component
@RequiredArgsConstructor
public class RedissonAspect {

    private final RedissonClient redissonClient;

    // @RedissonLock 어노테이션 적용 Lock Aspect
    @Around("@annotation(app.finup.infra.redisson.annotation.RedissonLock)")
    public Object redissonLock(ProceedingJoinPoint joinPoint) throws Throwable {

        // [1] 적용 메소드 및 어노테이션 정보 조회
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedissonLock redissonLock = signature.getMethod().getAnnotation(RedissonLock.class);

        // [2] 필요 정보 조회
        String key = redissonLock.key(); // LockKey

        // [3] Lock 객체 조회
        RLock lock = redissonClient.getLock(key);

        // [4] 어노테이션 내 파라미터 조회
        long waitTime = redissonLock.waitTime(); // Lock 획득 대기시간
        long leaseTime = redissonLock.leaseTime(); // Lock 최대 획득 시간 (최대 점유가능 시간)
        TimeUnit timeUnit = redissonLock.timeUnit(); // 시간 단위 (밀리초, 초, 분, ...)

        // [5] Lock 획득 수행
        return tryLock(joinPoint, lock, key, waitTime, leaseTime, timeUnit);
    }


    @Around("@annotation(app.finup.infra.redisson.annotation.RedissonMultiLock)")
    public Object redissonMultiLock(ProceedingJoinPoint joinPoint) throws Throwable {

        // [1] 적용 메소드 및 어노테이션 정보 조회
        MethodSignature signature = (MethodSignature) joinPoint.getSignature();
        RedissonMultiLock redissonMultiLock = signature.getMethod().getAnnotation(RedissonMultiLock.class);

        // [2] 필요 정보 조회
        String[] lockKeys = redissonMultiLock.keys(); // 여러 개의 key
        RLock[] locks = Arrays.stream(lockKeys)
                .map(redissonClient::getLock)
                .toArray(RLock[]::new);

        // [3] MultiLock 객체 조회
        RLock lock = redissonClient.getMultiLock(locks);

        // [4] 어노테이션 내 파라미터 조회
        long waitTime = redissonMultiLock.waitTime(); // Lock 획득 대기시간
        long leaseTime = redissonMultiLock.leaseTime(); // Lock 최대 획득 시간 (최대 점유가능 시간)
        TimeUnit timeUnit = redissonMultiLock.timeUnit(); // 시간 단위 (밀리초, 초, 분, ...)
        String strLockKeys = String.join(", ", lockKeys);

        // [5] Lock 획득 수행
        return tryLock(joinPoint, lock, strLockKeys, waitTime, leaseTime, timeUnit);
    }


    // Lock 획득 시도 로직
    private Object tryLock(
            ProceedingJoinPoint joinPoint,
            RLock lock,
            String key,
            long waitTime,
            long leaseTime,
            TimeUnit timeUnit) throws Throwable {

        try {
            // Lock 획득 시도
            boolean isAvailable = lock.tryLock(
                    waitTime,    // Lock 획득 대기시간
                    leaseTime,   // Lock 최대 획득 시간 (최대 점유가능 시간)
                    timeUnit     // 시간 단위 (밀리초, 초, 분, ...)
            );

            // Lock 획득에 실패한 경우 처리
            if (!isAvailable) throw new LockException(AppStatus.LOCK_ALREADY_EXISTS);

            // Lock 획득 처리
            LogUtils.showInfo(this.getClass(), LogEmoji.LOCK, "Lock 획득 성공. Key : %s", key);
            return joinPoint.proceed();

            // 위에서 던진 커스텀 예외는 다시 던짐
        } catch (LockException e) {
            LogUtils.showError(this.getClass(), "Lock 획득 실패. 이미 Lock이 존재합니다. Key : %s", key);
            throw e;

            // 인터럽트 발생
        } catch (IllegalArgumentException e) {
            LogUtils.showError(this.getClass(), "Lock 획득 실패. @RedissonLock 설정이 올바르지 않습니다. Key : %s\n원인: %s", key, e.getMessage());
            throw new LockException(AppStatus.LOCK_ACQUIRE_FAILED);

            // 인터럽트 발생
        } catch (RedisException e) {
            LogUtils.showError(this.getClass(), "Lock 획득 실패. Redis 서버 연결에 실패했습니다. Key : %s\n원인: %s", key, e.getMessage());
            throw new LockException(AppStatus.LOCK_ACQUIRE_FAILED);

            // 인터럽트 발생
        } catch (InterruptedException e) {
            LogUtils.showError(this.getClass(), "Lock 획득 중 인터럽트 발생. Key : %s", key);
            Thread.currentThread().interrupt();
            throw new LockException(AppStatus.LOCK_ACQUIRE_FAILED);

            // 현재 스레드가 LOCK을 보유하고 있는지 확인 후 헤제 (모든 로직이 실행된 후 후처리)
        } finally {
            try {
                if (Objects.nonNull(lock) && lock.isHeldByCurrentThread()) {
                    lock.unlock();
                    LogUtils.showInfo(this.getClass(), LogEmoji.UNLOCK, "Lock 해제 완료. Key : %s", key);
                }

                // 이미 헤제된 Lock을 또 헤제하려는 경우
            } catch (IllegalMonitorStateException e) {
                LogUtils.showWarn(this.getClass(), "이미 헤제된 Lock 헤제 시도. Key : %s", key);

                // 기타 사유로 Lock 헤제에 실패한 경우
            } catch (Exception e) {
                LogUtils.showError(this.getClass(), "Lock 헤제 처리 중 예상 외 오류 발생. Key : %s\n오류 : %s", key, e.getMessage());
            }
        }
    }
}
