package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizDto;


/**
 * 퀴즈 비즈니스 로직 제공 인터페이스
 * @author kcw
 * @since 2026-01-13
 */
public interface QuizService {

    /**
     * 퀴즈 결과 기록
     * @param rq 퀴즈 결과 기록요청 DTO
     */
    void record(QuizDto.Add rq);
}
