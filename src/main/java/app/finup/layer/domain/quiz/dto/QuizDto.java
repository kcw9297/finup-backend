package app.finup.layer.domain.quiz.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;


/**
 * 퀴즈 정보를 담기 위한 DTO 클래스
 * @author kcw
 * @since 2026-01-10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizDto {

    /**
     * 점수 저장 요청
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Add {
        private Integer score;
        private Long memberId;
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
}
