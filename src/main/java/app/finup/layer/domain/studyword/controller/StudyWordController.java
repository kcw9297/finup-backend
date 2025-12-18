package app.finup.layer.domain.studyword.controller;


import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.studyword.service.StudyWordRecommendService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 개념 학습 단어 REST API 클래스
 * @author kcw
 * @since 2025-12-10
 */

@Slf4j
@RestController
@RequestMapping(Url.STUDY_WORD)
@RequiredArgsConstructor
@Validated // 파일 검증
public class StudyWordController {

    private final StudyWordRecommendService studyWordRecommendService;


    @GetMapping("/recommend/study")
    public ResponseEntity<?> recommendStudy(@AuthenticationPrincipal CustomUserDetails userDetails,
                                            @RequestParam Long studyId,
                                            @RequestParam boolean retry) {

        // [1] 현재 로그인 회원 정보
        Long memberId = userDetails.getMemberId();

        // [2] 재시도 여부에 따라 추천 영상 반환 (캐시 여부)
        return retry ?
                Api.ok(studyWordRecommendService.retryRecommendForStudy(memberId, studyId)) :
                Api.ok(studyWordRecommendService.recommendForStudy(memberId, studyId));
    }

}
