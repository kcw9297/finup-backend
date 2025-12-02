package app.finup.layer.domain.studyprogress.dto;

import lombok.*;

import java.util.Objects;

/**
 * StudyProgress(학습 진도) DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyProgressDto {

    /**
     * 리스트 결과로 사용
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        private Long memberStudyId;
        private Long studyId;
        private String studyStatus;
    }

}