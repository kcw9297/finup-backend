package app.finup.layer.domain.quiz.service;

import app.finup.layer.domain.quiz.dto.QuizAiDto;
import java.util.List;

/**
 * 사용자 수준 퀴즈 AI 기능 제공 인터페이스
 * @author kcw
 * @since 2026-01-10
 */
public interface QuizAiService {

    /**
     * 퀴즈 질문 생성
     * @param memberId 퀴즈 요청 회원번호
     * @return 생성된 퀴즈 DTO 목록
     */
    List<QuizAiDto.Question> generateQuestions(Long memberId);
}
