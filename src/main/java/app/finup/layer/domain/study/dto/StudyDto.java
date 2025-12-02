package app.finup.layer.domain.study.dto;

import lombok.*;

/**
 * Study(개념학습) DTO 클래스
 * @author kcw
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyDto {

    /**
     * 리스트 결과로 사용
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {

        private Long levelId;
        private String name;
        private String description;
        private Integer levelNumber;
    }
}