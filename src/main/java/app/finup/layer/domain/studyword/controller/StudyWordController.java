package app.finup.layer.domain.studyword.controller;


import app.finup.common.constant.Url;
import app.finup.layer.domain.studyword.service.StudyWordService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
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

    private final StudyWordService studyWordService;



}
