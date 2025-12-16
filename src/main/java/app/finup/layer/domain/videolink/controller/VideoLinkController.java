package app.finup.layer.domain.videolink.controller;


import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.videolink.service.VideoLinkService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

/**
 * 학습용 영상 정보 REST API 클래스
 * @author kcw
 * @since 2025-12-16
 */

@Slf4j
@RestController
@RequestMapping(Url.VIDEO_LINK)
@RequiredArgsConstructor
public class VideoLinkController {

    private final VideoLinkService videoLinkService;

    @GetMapping("/recommend/study")
    public ResponseEntity<?> recommendForStudy(@RequestParam Long studyId,
                                               @RequestParam boolean retry) {

        return Api.ok(videoLinkService.recommendForStudy(studyId, retry));
    }

}
