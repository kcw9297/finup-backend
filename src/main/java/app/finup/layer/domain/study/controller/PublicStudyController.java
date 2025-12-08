package app.finup.layer.domain.study.controller;


import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.study.dto.StudyDto;
import app.finup.layer.domain.study.service.StudyService;
import app.finup.layer.domain.studyprogress.service.StudyProgressService;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.service.StudyWordService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 단계 학습 공개용 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.STUDY_PUBLIC)
@RequiredArgsConstructor
public class PublicStudyController {

    private final StudyService studyService;
    private final StudyProgressService studyProgressService;
    private final StudyWordService studyWordService;

    /**
     * 페이징 리스트 조회 (무한 스크롤)
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
     * 학습 정보 추가
     * [POST] /studies
     * @param rq 학습정보 추가 요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody StudyDto.Add rq) {

        // [1] 추가 수행
        Long studyId = studyService.add(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_OK_ADD, studyId);
    }


    /**
     * 학습 정보 추가
     * [PUT] /{studyId}
     * @param rq 학습정보 추가 요청 DTO
     */
    @PutMapping("/{studyId:[0-9]+}")
    public ResponseEntity<?> edit(@PathVariable Long studyId,
                                  @RequestBody StudyDto.Edit rq) {

        // [1] 갱신 수행
        rq.setStudyId(studyId);
        studyService.edit(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_OK_EDIT);
    }


    /**
     * 학습 데이터 삭제
     * [DELETE] /{studyId}
     */
    @DeleteMapping("/{studyId:[0-9]+}")
    public ResponseEntity<?> remove(@PathVariable Long studyId) {

        // [1] 삭제 수행
        studyService.remove(studyId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_OK_REMOVE);
    }


    /**
     * 학습 시작 (현재 학습의 학습 진도를 생성)
     * [POST] /{studyId}/progress/proceed
     */
    @PostMapping("/{studyId:[0-9]+}/progress/start")
    public ResponseEntity<?> startStudyProgress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                @PathVariable Long studyId) {

        // [1] 갱신 수행
        Long memberId = userDetails.getMemberId();
        studyProgressService.start(studyId, memberId);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 현재 학습의 진도를 완료 상태로 변경
     * [PATCH] /{studyId}/progress/complete
     */
    @PatchMapping("/{studyId:[0-9]+}/progress/complete")
    public ResponseEntity<?> proceedStudyProgress(@AuthenticationPrincipal CustomUserDetails userDetails,
                                                  @PathVariable Long studyId) {

        // [1] 갱신 수행
        Long memberId = userDetails.getMemberId();
        studyProgressService.complete(studyId, memberId);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 학습정보에 속하는 단어 일괄 조회
     * [GET] /{studyId}/words
     */
    @GetMapping("/{studyId:[0-9]+}/words")
    public ResponseEntity<?> getListByStudy(@PathVariable Long studyId) {
        return Api.ok(studyWordService.getListByStudy(studyId));
    }


    /**
     * 학습정보 내 단어 추가
     * [POST] /{studyId}/words
     * @param rq 학습단어 추가 요청 DTO
     */
    @PostMapping("/{studyId:[0-9]+}/words")
    public ResponseEntity<?> addWord(@PathVariable Long studyId,
                                     @RequestBody StudyWordDto.Add rq) {

        // [1] 생성 수행
        rq.setStudyId(studyId);
        studyWordService.add(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_ADD);
    }


    /**
     * 학습정보 내 단어 재정렬
     * [PATCH] /{studyId}/words/reorder
     * @param rq 학습단어 추가 요청 DTO
     */
    @PatchMapping("/{studyId:[0-9]+}/words/{studyWordId:[0-9]+}/reorder")
    public ResponseEntity<?> reorderWord(@PathVariable Long studyId,
                                         @PathVariable Long studyWordId,
                                         @RequestBody StudyWordDto.Reorder rq) {

        // [1] 재정렬 수행
        rq.setIds(studyId, studyWordId);
        studyWordService.reorder(rq);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 학습정보 내 단어 수정
     * [PUT] /{studyId}/words/{studyWordId}
     * @param rq 학습단어 변경 요청 DTO
     */
    @PutMapping("/{studyId:[0-9]+}/words/{studyWordId:[0-9]+}")
    public ResponseEntity<?> editWord(@PathVariable String studyId,
                                      @PathVariable Long studyWordId,
                                      @RequestBody StudyWordDto.Edit rq) {

        // [1] 갱신 수행
        rq.setStudyWordId(studyWordId);
        studyWordService.edit(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_OK_EDIT);
    }


    /**
     * 학습정보 내 단어 삭제
     * [DELETE] /{studyId}/words/{studyWordId}
     */
    @DeleteMapping("/{studyId:[0-9]+}/words/{studyWordId:[0-9]+}")
    public ResponseEntity<?> removeWord(@PathVariable String studyId,
                                        @PathVariable Long studyWordId) {

        // [1] 삭제 수행
        studyWordService.remove(studyWordId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_OK_REMOVE);
    }
}
