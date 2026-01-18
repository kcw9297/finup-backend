package app.finup.layer.domain.notice.scheduler;

import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.layer.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 공지사항 스케줄러 클래스
 * @author kcw
 * @since 2025-12-15
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class NoticeScheduler {

    private final NoticeService noticeService;

    @Scheduled(fixedDelay = 10, initialDelay = 3, timeUnit = TimeUnit.SECONDS)
    @Async(AsyncMode.NORMAL)
    public void syncViewCount() {
        LogUtils.runMethodAndShowCostLog("공지사항 조회수 동기화", noticeService::syncViewCount);
    }
}
