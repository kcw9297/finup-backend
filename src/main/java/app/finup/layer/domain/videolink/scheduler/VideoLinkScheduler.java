package app.finup.layer.domain.videolink.scheduler;

import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.TimeUtils;
import app.finup.infra.redisson.annotation.RedissonLock;
import app.finup.layer.domain.videolink.constant.VideoLinkRedisKey;
import app.finup.layer.domain.videolink.service.VideoLinkAiService;
import app.finup.layer.domain.videolink.service.VideoLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 학습 영상과 관련한 스케줄링 요청을 처리하기 위한 클래스
 * @author kcw
 * @since 2025-12-11
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class VideoLinkScheduler {

    private final VideoLinkService videoLinkService;
    private final VideoLinkAiService videoLinkAiService;


    // 유튜브 영상 동기화
    @Scheduled(cron = "0 0 0 * * *", zone = TimeUtils.ZONE_KOREA)
    @Async(AsyncMode.NORMAL)
    public void syncVideoLinks() {
        LogUtils.runMethodAndShowCostLog("학습 영상 동기화", videoLinkService::sync);
    }


    /**
     * 매 0시마다 홈 유튜브 추쳔 영상 갱신
     */
    @RedissonLock(key = VideoLinkRedisKey.LOCK_RECOMMEND_HOME_LOGOUT)
    @Scheduled(initialDelay = 5000, fixedDelay = Long.MAX_VALUE) // 최초 1회
    @Async(AsyncMode.NORMAL)
    public void initHomeVideoLinks() {
        LogUtils.runMethodAndShowCostLog("페이지 홈 추천 영상 초기화", videoLinkAiService::recommendForLogoutHome);
    }


    /**
     * 매 0시마다 홈 유튜브 추쳔 영상 갱신
     */
    @RedissonLock(key = VideoLinkRedisKey.LOCK_RECOMMEND_HOME_LOGOUT)
    @Scheduled(cron = "0 0 0 * * *", zone = TimeUtils.ZONE_KOREA)
    @Async(AsyncMode.NORMAL)
    public void updateRecommendHomeVideoLinks() {
        LogUtils.runMethodAndShowCostLog("페이지 홈 추천 영상 변경", videoLinkAiService::recommendForLogoutHome);
    }



}
