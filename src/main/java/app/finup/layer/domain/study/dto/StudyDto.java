package app.finup.layer.domain.study.dto;

import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.NoSpecialText;
import app.finup.layer.base.validation.annotation.PartSpecialText;
import app.finup.layer.base.validation.annotation.Select;
import app.finup.layer.base.validation.annotation.Text;
import lombok.*;

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
        private String detail;
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
        private String detail;
        private String aiAnalyzation;
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

        @PartSpecialText(min = 2, max = 30)
        private String name;

        @PartSpecialText(min = 5, max = 40)
        private String summary;

        @Text(min = 1, max = 300)
        private String detail; // 관리자만 조작할 수 있는, AI 추천을 위한문장

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

        @NoSpecialText(min = 2, max = 30)
        private String name;

        @NoSpecialText(min = 5, max = 40)
        private String summary;

        @Text(min = 1, max = 300)
        private String detail; // 관리자만 조작할 수 있는, AI 추천을 위한문장

        @Select
        private Integer level;
    }





}