package app.finup.layer.domain.stock.scheduler;

import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.infra.redisson.annotation.RedissonLock;
import app.finup.infra.redisson.annotation.RedissonMultiLock;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.service.NewsSchedulerService;
import app.finup.layer.domain.stock.constant.StockRedisKey;
import app.finup.layer.domain.stock.service.StockSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 주식 스케줄링 로직을 처리하는 클래스
 * @author kcw
 * @since 2025-12-25
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {

    // 사용 의존성
    private final StockSchedulerService stockSchedulerService;
    private final NewsSchedulerService newsSchedulerService;


    // 주식 AT 발급 스케줄링
    @RedissonLock(key = StockRedisKey.LOCK_ISSUE_TOKEN)
    @Scheduled(initialDelay = 6, fixedDelay = 6, timeUnit = TimeUnit.HOURS)
    @Async(AsyncMode.NORMAL)
    public void issueToken() {
        LogUtils.runMethodAndShowCostLog("주식 API AccessToken 발급", stockSchedulerService::issueToken);
    }


    // 주식 정보 초기화 스케줄링 (단 한번만 수행)
    @RedissonMultiLock(keys = {StockRedisKey.LOCK_SYNC, NewsRedisKey.LOCK_SYNC_STOCK}) // MultiLock
    @Scheduled(initialDelay = 30000, fixedDelay = Long.MAX_VALUE)
    @Async(AsyncMode.STOCK_ASYNC)
    public void initSync(){

        // [1] 종목 초기화
        LogUtils.runMethodAndShowCostLog("주식 종목 초기화", stockSchedulerService::sync);

        // [2] 종목 내 뉴스 초기화
        LogUtils.runMethodAndShowCostLog("주식 종목 내 기사 초기화", newsSchedulerService::syncStock);
    }


    // 주식 정보 동기화 스케줄링
    @RedissonMultiLock(keys = {StockRedisKey.LOCK_SYNC, NewsRedisKey.LOCK_SYNC_STOCK}) // MultiLock
    @Scheduled(cron = "0 0,30 9-16 * * MON-FRI")
    @Async(AsyncMode.STOCK_ASYNC)
    public void sync(){

        // [1] 종목 동기화
        LogUtils.runMethodAndShowCostLog("주식 종목 동기화 시작", stockSchedulerService::sync);

        // [2] 종목 내 뉴스 동기화
        LogUtils.runMethodAndShowCostLog("주식 종목 내 기사 동기화 시작", newsSchedulerService::syncStock);
    }

}
