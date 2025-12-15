package app.finup.layer.domain.studyprogress.dto;

import lombok.*;

/**
 * StudyProgress(학습 진도) DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyProgressDto {

    /**
     * 리스트 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        private Long studyProgressId;
        private Long studyId;
        private String studyStatus;
    }

}