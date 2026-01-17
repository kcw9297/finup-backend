package app.finup.layer.domain.indicator.scheduler;

import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.TimeUtils;
import app.finup.infra.redisson.annotation.RedissonLock;
import app.finup.layer.domain.indicator.constant.IndicatorRedisKey;
import app.finup.layer.domain.indicator.service.IndicatorSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class IndicatorScheduler {

    // 사용 의존성
    private final IndicatorSchedulerService indicatorSchedulerService;


    @RedissonLock(key = IndicatorRedisKey.LOCK_SYNC_INDEX_FINANCIAL)
    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE) // 최초 1회
    @Async(AsyncMode.NORMAL)
    public void initFinancialIndex() {
        LogUtils.runMethodAndShowCostLog("금융 지표 초기화", indicatorSchedulerService::syncFinancialIndex);
    }


    @RedissonLock(key = IndicatorRedisKey.LOCK_SYNC_INDEX_FINANCIAL)
    @Scheduled(cron = "0 10 11 * * MON-FRI", zone = TimeUtils.ZONE_KOREA)
    @Async(AsyncMode.NORMAL)
    public void syncFinancialIndex() {
        LogUtils.runMethodAndShowCostLog("금융 지표 동기화", indicatorSchedulerService::syncFinancialIndex);
    }


    @RedissonLock(key = IndicatorRedisKey.LOCK_SYNC_INDEX_MARKET)
    @Scheduled(initialDelay = 1000, fixedDelay = Long.MAX_VALUE) // 최초 1회
    @Async(AsyncMode.NORMAL)
    public void initMarketIndex() {
        LogUtils.runMethodAndShowCostLog("주식 시장 지표 초기화", indicatorSchedulerService::syncMarketIndex);
    }


    @RedissonLock(key = IndicatorRedisKey.LOCK_SYNC_INDEX_MARKET)
    @Scheduled(cron = "0 10 13 * * MON-FRI", zone = TimeUtils.ZONE_KOREA)
    @Async(AsyncMode.NORMAL)
    public void syncMarketIndex() {
        LogUtils.runMethodAndShowCostLog("주식 시장 지표 동기화", indicatorSchedulerService::syncMarketIndex);
    }
}