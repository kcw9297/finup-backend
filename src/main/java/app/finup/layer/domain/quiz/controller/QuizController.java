package app.finup.layer.domain.quiz.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.quiz.dto.QuizDto;
import app.finup.layer.domain.quiz.service.QuizAiService;
import app.finup.layer.domain.quiz.service.QuizService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 수준테스트 REST API 클래스
 * @author lky
 * @since 2025-12-16
 */
@Slf4j
@RequestMapping(Url.QUIZ)
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizAiService quizAiService;
    private final QuizService quizService;

    /**
     * 수준 테스트 AI 생성
     * [GET] quiz/getQuestion
     */
    @GetMapping("/getQuestion")
    public ResponseEntity<?> getQuestion() {
        return Api.ok(quizAiService.getQuizAi());
    }

    /**
     * 수준 테스트 결과 저장
     * [POST] quiz/result
     */
    @PostMapping("/result")
    public ResponseEntity<?> saveResult(@AuthenticationPrincipal CustomUserDetails userDetails, @RequestBody QuizDto.Add rq) {
        Long memberId = userDetails.getMemberId();
        int score = rq.getScore();
        log.info("memberId 잘 가져와지냐?: {}", memberId.toString());
        log.info("score 잘 가져와지냐?: {}", score);
        quizService.save(memberId, score);
        return Api.ok();
    }
}
