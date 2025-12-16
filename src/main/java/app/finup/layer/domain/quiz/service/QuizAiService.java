package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizDto;
import app.finup.layer.domain.studyword.dto.StudyWordDto;

import java.util.List;

public interface QuizAiService {
    List<QuizDto.Question> getQuizAi(StudyWordDto.Quiz studyword);
}
