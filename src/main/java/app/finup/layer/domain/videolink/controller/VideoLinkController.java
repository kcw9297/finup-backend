package app.finup.layer.domain.videolink.controller;


import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.videolink.service.VideoLinkRecommendService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
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

    private final VideoLinkRecommendService videoLinkRecommendService;


    @GetMapping("/recommend/home")
    public ResponseEntity<?> recommendForHome(@AuthenticationPrincipal CustomUserDetails userDetails,
                                              @RequestParam boolean retry) {

        // [1] 현재 로그인 회원 정보
        Long memberId = userDetails.getMemberId();

        // [2] 재시도 여부에 따라 추천 영상 반환 (캐시 여부)
        return retry ?
                Api.ok(videoLinkRecommendService.retryRecommendForLoginHome(memberId)) :
                Api.ok(videoLinkRecommendService.recommendForLoginHome(memberId));
    }


    @GetMapping("/recommend/study")
    public ResponseEntity<?> recommendForStudy(@AuthenticationPrincipal CustomUserDetails userDetails,
                                               @RequestParam Long studyId,
                                               @RequestParam boolean retry) {

        // [1] 현재 로그인 회원 정보
        Long memberId = userDetails.getMemberId();

        // [2] 재시도 여부에 따라 추천 영상 반환 (캐시 여부)
        return retry ?
                Api.ok(videoLinkRecommendService.retryRecommendForStudy(studyId, memberId)) :
                Api.ok(videoLinkRecommendService.recommendForStudy(studyId, memberId));
    }

}
