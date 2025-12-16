package app.finup.layer.domain.quiz.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.quiz.service.QuizAiService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(Url.QUIZ)
@RestController
@RequiredArgsConstructor
public class QuizController {

    private final QuizAiService quizAiService;

    /**
     * 수준 테스트 AI 생성
     * [GET] quiz/getQuestion
     */
    @GetMapping("/getQuestion")
    public ResponseEntity<?> getQuestion()
    {
        return Api.ok(quizAiService.getQuizAi());
    }
}
