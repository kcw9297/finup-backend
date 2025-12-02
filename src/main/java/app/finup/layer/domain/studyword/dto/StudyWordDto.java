package app.finup.layer.domain.studyword.dto;

import lombok.*;


/**
 * 단계별 학습 단어 DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyWordDto {


    /**
     * 리스트 결과로 사용
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {

        private Long studyWordId;
        private String name;
        private String meaning;
    }


    /**
     * 새로운 단어 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        private String name;
        private String meaning;
        private Long studyId;
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
        private String name;
        private String meaning;
    }


    /**
     * 단어 순서 재정렬
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Reorder {

        private Long studyWordId;
        private Long beforeStudyWordId;
        private Long afterStudyWordId;
    }

}