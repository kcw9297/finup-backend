package app.finup.layer.domain.video.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.video.dto.VideoDto;
import app.finup.layer.domain.video.service.VideoService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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
public class AdminVideoController {
    private final VideoService videoService;

    @GetMapping("/{videoId}")
    public ResponseEntity<?> getVideoDetail(@PathVariable String videoId) {
        // 유튜브 URL로 변환
        String videoUrl = "https://www.youtube.com/watch?v=" + videoId;

        VideoDto.Detail detail = videoService.getDetail(videoUrl);

        return Api.ok(detail);
    }


    @PostMapping
    public ResponseEntity<?> add(@RequestBody VideoDto.Detail rq) {
        String url = rq.getVideoUrl();
        log.info("YouTube API 요청 URL: {}", url);

        VideoDto.Detail video = videoService.getDetail(url);
        return Api.ok(video);
    }
}
