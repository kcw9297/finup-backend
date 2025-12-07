package app.finup.layer.domain.studyword.controller;


import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.studyword.service.StudyWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

/**
 * 단계 학습 단어 관리자용 REST API 클래스
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


    @PostMapping("/{studyWordId:[0-9]+}/image")
    public ResponseEntity<?> uploadWordImage(@PathVariable Long studyWordId,
                                             @RequestParam MultipartFile wordImage) {

        // [1] 이미지 업로드 수행
        studyWordService.uploadImage(studyWordId, wordImage);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_UPLOAD_IMAGE);
    }


    @DeleteMapping("/{studyWordId:[0-9]+}/image")
    public ResponseEntity<?> editWordImage(@PathVariable Long studyWordId) {

        // [1] 이미지 업로드 수행
        studyWordService.removeImage(studyWordId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.STUDY_WORD_OK_REMOVE_IMAGE);
    }
}
