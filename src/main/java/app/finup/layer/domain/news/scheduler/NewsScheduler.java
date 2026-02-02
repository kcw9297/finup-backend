package app.finup.layer.domain.news.scheduler;

import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.TimeUtils;
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


    // 기사 정보 초기화 스케줄링 (단 한번만 수행)
    @RedissonLock(key = NewsRedisKey.LOCK_SYNC)
    @Scheduled(initialDelay = 140000, fixedDelay = Long.MAX_VALUE)
    @Async(AsyncMode.NEWS)
    public void initSync(){
        LogUtils.runMethodAndShowCostLog("메인 기사 초기화", newsSchedulerService::syncMain);
        LogUtils.runMethodAndShowCostLog("종목 기사 동기화", newsSchedulerService::syncStock);
    }


    // 메인 뉴스 조회 및 본문 크롤링 (매 시간마다)
    @RedissonLock(key = NewsRedisKey.LOCK_SYNC)
    @Scheduled(cron = "0 5 * * * *", zone = TimeUtils.ZONE_KOREA)
    @Async(AsyncMode.NEWS)
    public void sync(){
        LogUtils.runMethodAndShowCostLog("메인 기사 초기화", newsSchedulerService::syncMain);
        LogUtils.runMethodAndShowCostLog("종목 기사 동기화", newsSchedulerService::syncStock);
    }


    // 오래된 기사 정리 (매 0시마다 수행)
    @Scheduled(cron = "0 0 0 * * *", zone = TimeUtils.ZONE_KOREA)
    @Async(AsyncMode.NEWS)
    public void clean(){
        LogUtils.runMethodAndShowCostLog("오래된 뉴스 정리", newsSchedulerService::clean);
    }

}
