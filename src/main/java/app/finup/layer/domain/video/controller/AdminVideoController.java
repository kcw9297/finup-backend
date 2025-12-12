package app.finup.layer.domain.video.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.base.validation.annotation.YouTubeUrl;
import app.finup.layer.domain.video.dto.VideoDto;
import app.finup.layer.domain.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 학습용 영상 정보 가져오는 REST API 클래스
 * @author khj
 * @since 2025-12-10
 */

@Slf4j
@RestController
@RequestMapping(Url.VIDEO_ADMIN)
@RequiredArgsConstructor
@Validated
public class AdminVideoController {
    private final VideoService videoService;

    @GetMapping
    public ResponseEntity<?> getVideoDetail(@YouTubeUrl String videoUrl) {
        return Api.ok(videoService.getDetail(videoUrl));
    }
}
