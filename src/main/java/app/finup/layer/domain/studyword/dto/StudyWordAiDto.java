package app.finup.layer.domain.studyword.dto;

import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.PartSpecialText;
import app.finup.layer.base.validation.annotation.Text;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;


/**
 * 단계별 학습 단어 DTO 클래스
 * @author kcw
 * @since 2025-12-02
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyWordAiDto {

    /**
     * 단어 정보 수정
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Recommendation {

        private StudyInfo study;
        private List<WordCandidate> candidates;
        private List<Long> latestStudyWordIds;

        @Data
        @Builder
        @AllArgsConstructor
        public static class StudyInfo {

            private String name;
            private String summary;
            private String detail;
            private int level;
        }


        @Data
        @Builder
        @AllArgsConstructor
        public static class WordCandidate {

            private long studyWordId;
            private String name;
            private String meaning;
        }
    }


}