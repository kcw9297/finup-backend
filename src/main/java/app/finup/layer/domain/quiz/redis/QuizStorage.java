package app.finup.layer.domain.quiz.redis;

import app.finup.layer.domain.quiz.dto.QuizDto;

import java.util.List;

/**
 * quiz 관련 데이터 캐싱을 위한 저장 인터페이스
 * @author lky
 * @since 2025-12-17
 */
public interface QuizStorage {

    //AI 퀴즈 문제 여러 개
    void addQuiz(List<QuizDto.Question> quiz);
    List<List<QuizDto.Question>> getQuiz();
}
