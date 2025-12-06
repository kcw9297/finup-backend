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

        private String name;
        private String meaning;
        private Long studyId;
        private Long lastStudyWordId; // 추가 하기 직전, 가장 마지막에 있던 단어번호
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

        private Long studyId; // 재정렬 시 필요
        private Long studyWordId;
        private Long prevStudyWordId;
        private Long nextStudyWordId;
    }

}