package app.finup.layer.domain.videolink.scheduler;

import app.finup.common.utils.LogUtils;
import app.finup.layer.domain.videolink.service.VideoLinkRecommendService;
import app.finup.layer.domain.videolink.service.VideoLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

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
    private final VideoLinkRecommendService videoLinkRecommendService;

    /**
     * 10분마다 유튜브 영상 동기화
     */
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES, initialDelay = 0)
    public void syncVideoLinks() {
        videoLinkService.sync();
        LogUtils.showInfo(this.getClass(), "학습 영상 동기화 수행 완료");
    }


    /**
     * 매 30분마다 유튜브 홈 영상 초기화
     */
    @Scheduled(fixedDelay = 30, timeUnit = TimeUnit.MINUTES, initialDelay = 0)
    public void initHomeVideoLinks() {
        videoLinkRecommendService.recommendForLogoutHome();
        LogUtils.showInfo(this.getClass(), "페이지 홈 추천 영상 조회 완료");
    }


}
