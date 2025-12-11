package app.finup.layer.domain.study.dto;

import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.NoSpecialText;
import app.finup.layer.base.validation.annotation.Select;
import app.finup.layer.base.validation.annotation.Text;
import lombok.*;

import java.util.Objects;

/**
 * Study(개념학습) DTO 클래스
 * @author kcw
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyDto {

    /**
     * 리스트 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        private Long studyId;
        private String name;
        private String summary;
        private Integer level;
    }


    /**
     * 개념 상세 조회
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

        private Long studyId;
        private String name;
        private String summary;
        private String description;
        private Integer level;
    }

    /**
     * 검색 요청
     */
    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class Search extends SearchRequest {

        private String order = "latest";
    }


    /**
     * 개념 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        @NoSpecialText(min = 5, max = 20)
        private String name;

        @NoSpecialText(min = 5, max = 20)
        private String summary;

        @Text(min = 10, max = 100)
        private String description;

        @Select
        private Integer level;
    }


    /**
     * 개념 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edit {

        private Long studyId;

        @NoSpecialText(min = 5, max = 20)
        private String name;

        @NoSpecialText(min = 5, max = 20)
        private String summary;

        @Text(min = 10, max = 100)
        private String description;

        @Select
        private Integer level;
    }





}