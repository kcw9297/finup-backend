package app.finup.layer.domain.wordQuiz.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.wordQuiz.dto.WordQuizDto;
import app.finup.layer.domain.wordQuiz.service.WordQuizService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 오늘의 퀴즈 REST API
 * @author khj
 * @since 2025-12-15
 */
@Slf4j
@RestController
@RequiredArgsConstructor
@RequestMapping(Url.WORD_QUIZ)
public class WordQuizController {

    private final WordQuizService wordQuizService;

    /**
     * 오늘의 퀴즈 조회
     * [GET] /api/word-quizzes/today
     */
    @GetMapping("/today")
    public ResponseEntity<?> getTodayQuiz() {

        WordQuizDto.Today rp = wordQuizService.getTodayQuiz();
        return Api.ok(rp);
    }

    /**
     * 퀴즈 정답 확인
     * [POST] /api/word-quizzes/today/answer
     */
    @PostMapping("/today/answer")
    public ResponseEntity<?> checkAnswer(
            @RequestBody WordQuizDto.Answer rq
    ) {

        Boolean correct = wordQuizService.checkAnswer(rq);
        return Api.ok(correct);
    }
}
