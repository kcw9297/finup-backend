package app.finup.layer.domain.level.dto;

import lombok.*;

/**
 * 개념 학습 단게 DTO 클래스
 * @author sjs
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class LevelDto {

    /**
     * 개념 단계 단일 조회(상세)용 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder

    public static class Detail {
        private Long levelId;
        private String name;
        private String description;
        private Integer orderNumber;
    }
    /**
     * 개념 단계 리스트 조회용 DTO
     * (프론트의 리스트 UI 한 줄 = Row 하나)
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private Long levelId;
        private String name;
        private String description;
        private Integer orderNumber;

        private Integer progress; //진척도%
        private String status; //학습상태
    }

    /**
     * 관리자 등록 요청 DTO
     * - 관리자 페이지에서 단계 생성할 때 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Write {
        private String name;
        private String description;
        private Integer orderNumber;
    }

    /**
     * 관리자 수정 요청 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Edit {
        private Long levelId;
        private String name;
        private String description;
        private Integer orderNumber;
    }

}