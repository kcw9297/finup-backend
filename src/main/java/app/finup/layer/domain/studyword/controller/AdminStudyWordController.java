package app.finup.layer.domain.studyword.controller;


import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.base.validation.annotation.ImageFile;
import app.finup.layer.domain.studyword.dto.StudyWordDto;
import app.finup.layer.domain.studyword.service.StudyWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 개념 학습 단어 관리자용 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.STUDY_WORD_ADMIN)
@RequiredArgsConstructor
@Validated // 파일 검증
public class AdminStudyWordController {

    private final StudyWordService studyWordService;

    /**
     * 개념 학습 단어 추가
     * [POST] /study-words/search
     * @param rq 단어 검색요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(StudyWordDto.Search rq) {

        // [1] 검색 수행
        Page<StudyWordDto.Row> rp = studyWordService.search(rq);

        // [2] 검색 응답 반환 (페이징 객체 포함)
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }


    /**
     * 개념 학습 단어 추가
     * [POST] /study-words
     * @param rq 학습단어 추가 요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> add(@RequestBody @Validated StudyWordDto.Add rq) {

        // [1] 생성 수행
        studyWordService.add(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_ADD);
    }


    /**
     * 단어 이미지 등록
     * [POST] /study-words/{studyWordId}/image
     * @param wordImage 업로드 단어 이미지 파일
     */
    @PostMapping("/{studyWordId:[0-9]+}/image")
    public ResponseEntity<?> uploadImage(@PathVariable Long studyWordId,
                                         @RequestParam @ImageFile MultipartFile wordImage) {

        // [1] 이미지 업로드 수행
        String imageUrl = studyWordService.uploadImage(studyWordId, wordImage);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_UPLOAD_IMAGE, imageUrl);
    }


    /**
     * 개념 학습 단어 수정
     * [PUT] /study-words/{studyWordId}
     * @param rq 학습단어 변경 요청 DTO
     */
    @PutMapping("/{studyWordId:[0-9]+}")
    public ResponseEntity<?> edit(@PathVariable Long studyWordId,
                                  @RequestBody @Validated StudyWordDto.Edit rq) {

        // [1] 갱신 수행
        rq.setStudyWordId(studyWordId);
        studyWordService.edit(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_EDIT);
    }


    /**
     * 개념 학습 단어 삭제
     * [DELETE] /study-words/{studyWordId}
     */
    @DeleteMapping("/{studyWordId:[0-9]+}")
    public ResponseEntity<?> remove(@PathVariable Long studyWordId) {

        // [1] 삭제 수행
        studyWordService.remove(studyWordId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_REMOVE);
    }


    /**
     * 단어 이미지 삭제
     * [DELETE] /study-words/{studyWordId}/image
     */
    @DeleteMapping("/{studyWordId:[0-9]+}/image")
    public ResponseEntity<?> removeImage(@PathVariable Long studyWordId) {

        // [1] 이미지 업로드 수행
        studyWordService.removeImage(studyWordId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_REMOVE_IMAGE);
    }

}
