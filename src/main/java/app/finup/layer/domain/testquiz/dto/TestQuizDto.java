package app.finup.layer.domain.testquiz.dto;

import lombok.*;
import java.util.*;

/**
 * 개념 테스트 점수 내역 DTO 클래스
 * @author phj
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestQuizDto {

    /**
     * 최신 점수 조회
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Detail {
        private Long testQuizId;
        private int score;
    }

    /**
     * 테스트 제출
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Submit {
        private List<Answer> answers;
    }

    /**
     * 유저의 답
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Answer {
        private Long testQuizRecordId;
        private Integer userAnswer;
    }

    /**
     * 테스트 결과 점수 반환
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    private static class Grade {
        private Integer score;
        private String level;
    }
}
