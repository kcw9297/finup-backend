package app.finup.layer.domain.testquizrecord.dto;

import lombok.*;

/**
 * 개념 테스트 질문 내역 DTO 클래스
 * @author phj
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TestQuizRecordDto {
    /**
     * 테스트를 위한 문제 상세 조회 (정답 제외)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail {
        private Long testQuizRecordId;
        private String content;
        private String choice1;
        private String choice2;
        private String choice3;
        private String choice4;
    }
}
