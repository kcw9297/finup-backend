package app.finup.layer.domain.videolink.controller;


import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.videolink.service.VideoLinkAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 학습용 영상 정보 공개용 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.VIDEO_LINK_PUBLIC)
@RequiredArgsConstructor
public class PublicVideoLinkController {

    private final VideoLinkAiService videoLinkAiService;

    /**
     * 추천 홈 화면 영상
     * [GET] /video-links/recommend/home
     */

    @GetMapping("/recommend/home")
    public ResponseEntity<?> recommendForHome() {
        return Api.ok(videoLinkAiService.recommendForLogoutHome());
    }

}
