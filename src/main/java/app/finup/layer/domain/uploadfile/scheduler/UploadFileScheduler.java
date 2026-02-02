package app.finup.layer.domain.uploadfile.scheduler;


import app.finup.common.constant.AsyncMode;
import app.finup.common.utils.LogUtils;
import app.finup.layer.domain.uploadfile.service.UploadFileSchedulerService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.concurrent.TimeUnit;

/**
 * 파일 스케줄링 로직을 처리하는 클래스
 * @author kcw
 * @since 2025-12-10
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class UploadFileScheduler {

    private final UploadFileSchedulerService uploadFileSchedulerService;


    /**
     * 고아 파일 삭제 스케줄링 로직
     */
    @Scheduled(fixedDelay = 10, timeUnit = TimeUnit.MINUTES)
    @Async(AsyncMode.NORMAL)
    public void removeOrphanFiles() {
        LogUtils.runMethodAndShowCostLog("고아 파일 삭제", uploadFileSchedulerService::clearOrphan);
    }
}
