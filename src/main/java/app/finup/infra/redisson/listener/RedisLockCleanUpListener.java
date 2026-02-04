package app.finup.infra.redisson.listener;

import app.finup.common.enums.LogEmoji;
import app.finup.common.utils.LogUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.redisson.api.RedissonClient;
import org.springframework.context.event.ContextClosedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * 애플리케이션 종료 시 Redisson Lock 정리를 처리하는 리스너 클래스
 * @author kcw
 * @since 2026-01-18
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class RedisLockCleanUpListener {

    private final RedissonClient redissonClient;

    @EventListener
    public void onApplicationClose(ContextClosedEvent event) {

        LogUtils.showInfo(this.getClass(), LogEmoji.LOCK, "애플리케이션 종료 감지 - Redisson Lock 정리 시작");

        try {
            long deletedCount = redissonClient.getKeys().deleteByPattern("LOCK:*");
            LogUtils.showInfo(this.getClass(), LogEmoji.UNLOCK, "Redisson Lock 정리 완료 - 삭제된 Lock 개수: {}", deletedCount);

        } catch (Exception e) {
            LogUtils.showError(this.getClass(), "Redisson Lock 정리 중 오류 발생", e);
        }
    }
}
