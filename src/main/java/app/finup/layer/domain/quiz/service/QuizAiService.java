package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizDto;
import java.util.List;

public interface QuizAiService {
    List<QuizDto.Question> getQuizAi();
    void refreshQuizAi();
}
