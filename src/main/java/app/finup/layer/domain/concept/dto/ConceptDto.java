package app.finup.layer.domain.concept.dto;

import lombok.*;

/**
 * Level List DTO 클래스
 * @author sjs
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ConceptDto {
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor

    public static class Summary {
        /**
         * 리스트 결과로 사용
         */
        private Long id;
        private String name;
        private String description;
        private Integer orderNumber;
    }
}