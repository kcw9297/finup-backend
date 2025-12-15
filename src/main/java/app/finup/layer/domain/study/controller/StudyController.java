package app.finup.layer.domain.study.controller;


import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.study.dto.StudyDto;
import app.finup.layer.domain.study.service.StudyService;
import app.finup.layer.domain.studyprogress.service.StudyProgressService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 단계 학습 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.STUDY)
@RequiredArgsConstructor
public class StudyController {

    private final StudyService studyService;
    private final StudyProgressService studyProgressService;

    /**
     * 페이징 리스트 조회
     * [GET] /studies/search
     * @param rq 페이징 요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> getPagedList(StudyDto.Search rq) {

        // [1] 페이징 조회
        Page<StudyDto.Row> page = studyService.getPagedList(rq);

        // [2] 조회 결과 반환
        return Api.ok(page.getRows(), Pagination.of(page));
    }


    /**
     * 학습 정보 단일 조회
     * [GET] /studies/{studyId}
     */
    @GetMapping("/{studyId:[0-9]+}")
    public ResponseEntity<?> getDetail(@PathVariable Long studyId) {
        return Api.ok(studyService.getDetail(studyId));
    }


    /**
     * 학습 시작 (현재 학습의 학습 진도를 생성)
     * [POST] /{studyId}/progress
     */
    @PostMapping("/{studyId:[0-9]+}/progress")
    public ResponseEntity<?> startStudyProgress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @PathVariable Long studyId) {

        // [1] 갱신 수행
        Long memberId = userDetails.getMemberId();
        studyProgressService.start(studyId, memberId);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 현재 학습의 진도를 진행 중 상태로 변경
     * [PATCH] /{studyId}/progress/in-progress
     */
    @PatchMapping("/{studyId:[0-9]+}/progress/in-progress")
    public ResponseEntity<?> progressStudyProgress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable Long studyId) {

        // [1] 갱신 수행
        Long memberId = userDetails.getMemberId();
        studyProgressService.progress(studyId, memberId);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 현재 학습의 진도를 완료 상태로 변경
     * [PATCH] /{studyId}/progress/complete
     */
    @PatchMapping("/{studyId:[0-9]+}/progress/complete")
    public ResponseEntity<?> completeStudyProgress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                   @PathVariable Long studyId) {

        // [1] 갱신 수행
        Long memberId = userDetails.getMemberId();
        studyProgressService.complete(studyId, memberId);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 현재 학습의 진도 초기화 (진도 정보 삭제)
     * [DELETE] /{studyId}/progress
     */
    @DeleteMapping("/{studyId:[0-9]+}/progress")
    public ResponseEntity<?> initializeStudyProgress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                     @PathVariable Long studyId) {

        // [1] 갱신 수행
        Long memberId = userDetails.getMemberId();
        studyProgressService.initialize(studyId, memberId);

        // [2] 성공 응답 반환
        return Api.ok();
    }


}
