package app.finup.layer.domain.quiz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizDto {
    /**
     * 점수 저장 요청
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Write {
        private Integer score;
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private Long quizId;
        private Integer score;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime cdate;
    }

    /**
     * AI로 생성된 문제
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Question {
        private String question;      // 문제
        private List<String> choices; // 보기
        private Integer answer;       // 정답 index
        private String explanation;   // 해설
    }
}
