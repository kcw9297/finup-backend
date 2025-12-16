package app.finup.layer.domain.studyword.dto;

import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.NoSpecialText;
import app.finup.layer.base.validation.annotation.Text;
import lombok.*;


/**
 * 단계별 학습 단어 DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyWordDto {

    /**
     * 검색 요청
     */
    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class Search extends SearchRequest {

        private String order = "latest";
        private String filter = "";
        private String keyword = "";

        public Search() {
            super(6); // 한 줄에는 3개 씩 총 2줄
        }
    }


    /**
     * 리스트 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        private Long studyWordId;
        private String name;
        private String meaning;
        private String imageUrl;
    }


    /**
     * 새로운 단어 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        @NoSpecialText(max = 20)
        private String name;

        @Text(min = 10, max = 100)
        private String meaning;
    }


    /**
     * 단어 정보 수정
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edit {

        private Long studyWordId;

        @NoSpecialText(max = 20)
        private String name;

        @Text(min = 10, max = 100)
        private String meaning;
    }

    /**
     * Quiz(수준 테스트) AI prompt용
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Quiz {
        private String name;
        private String meaning;
    }

}