package app.finup.layer.domain.wordQuiz.service;

import app.finup.layer.domain.wordQuiz.dto.WordQuizDto;


/**
 * 단어장 오늘의 퀴즈 서비스 인터페이스
 * @author khj
 * @since 2025-12-15
 */

public interface WordQuizService {

    /**
     * 오늘의 퀴즈 가져오기
     */
    WordQuizDto.Today getTodayQuiz();

    /**
     * 퀴즈 정답 확인
     */
    Boolean checkAnswer(WordQuizDto.Answer rq);
}
