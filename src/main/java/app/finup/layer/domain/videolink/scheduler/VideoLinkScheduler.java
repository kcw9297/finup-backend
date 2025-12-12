package app.finup.layer.domain.videolink.scheduler;

import app.finup.common.utils.LogUtils;
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

    /**
     * 10분마다 유튜브 영상 최신화
     */
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    public void syncVideoLinks() {
        videoLinkService.sync();
        LogUtils.showInfo(this.getClass(), "학습 영상 동기화 수행 완료");
    }
}
