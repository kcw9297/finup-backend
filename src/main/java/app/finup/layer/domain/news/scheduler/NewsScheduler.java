package app.finup.layer.domain.news.scheduler;

import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.infra.redisson.annotation.RedissonLock;
import app.finup.layer.domain.news.constant.NewsRedisKey;
import app.finup.layer.domain.news.service.NewsSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;


/**
 * 뉴스 스케줄링 로직을 처리하는 클래스
 * @author kcw
 * @since 2025-12-24
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class NewsScheduler {

    // 사용 의존성
    private final NewsSchedulerService newsSchedulerService;


    // 주식 정보 초기화 스케줄링 (단 한번만 수행)
    @RedissonLock(key = NewsRedisKey.LOCK_SYNC_MAIN)
    @Scheduled(initialDelay = 500, fixedDelay = Long.MAX_VALUE)
    @Async(AsyncMode.NEWS)
    public void initSyncMain(){
        LogUtils.runMethodAndShowCostLog("메인 기사 초기화", newsSchedulerService::syncMain);
    }


    // 메인 뉴스 조회 및 본문 크롤링
    @RedissonLock(key = NewsRedisKey.LOCK_SYNC_MAIN)
    @Scheduled(cron = "0 10,40 * * * *")
    @Async(AsyncMode.NEWS)
    public void syncMain(){
        LogUtils.runMethodAndShowCostLog("메인 뉴스 동기화", newsSchedulerService::syncMain);
    }


    // 매 시간 15분/45분 마다 종목 내 뉴스 크롤링
    @RedissonLock(key = NewsRedisKey.LOCK_SYNC_STOCK)
    @Scheduled(cron = "0 15,45 * * * *")
    @Async(AsyncMode.NEWS)
    public void syncStock(){
        LogUtils.runMethodAndShowCostLog("종목 뉴스 동기화", newsSchedulerService::syncStock);
    }


    // 오래된 뉴스 삭제 (Lock 불필요)
    @Scheduled(cron = "0 0 0/3 * * *")
    @Async(AsyncMode.NORMAL)
    public void removeOlds(){
        LogUtils.runMethodAndShowCostLog("오래된 뉴스 삭제", newsSchedulerService::removeOlds);
    }
}
