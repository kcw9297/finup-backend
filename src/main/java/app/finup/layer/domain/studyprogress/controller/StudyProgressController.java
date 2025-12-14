package app.finup.layer.domain.studyprogress.controller;


import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.studyprogress.service.StudyProgressService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 단계 학습 진도 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.STUDY_PROGRESS)
@RequiredArgsConstructor
public class StudyProgressController {

    private final StudyProgressService studyProgressService;

    /**
     * 현재 로그인 회원의 진도 정보 조회
     * [GET] /study-progresses/my
     */
    @GetMapping("/my")
    public ResponseEntity<?> getMyList(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getMemberId();
        return Api.ok(studyProgressService.getMyList(memberId));
    }
}
