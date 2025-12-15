package app.finup.layer.domain.wordQuiz.dto;

import lombok.*;

import java.util.List;

@Builder
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordQuizDto {

    @Data
    @AllArgsConstructor
    @Builder
    public static class Today {

        private Long termId;            // 기준 단어 ID
        private String question;        // 설명
        private List<String> choices;   // 보기
    }

    @Data
    public static class Answer {

        private Long termId;
        private String selected;
    }
}
