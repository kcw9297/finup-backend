package app.finup.layer.domain.quiz.dto;

import lombok.*;

import java.util.List;

/**
 * 퀴즈 AI 정보를 담기 위한 DTO 클래스
 * @author kcw
 * @since 2026-01-10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizAiDto {

    /**
     * AI 문제를 생성하기 위한 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Question {
        private String question;      // 문제
        private List<String> choices; // 보기
        private Integer answer;       // 정답 index
    }

}
