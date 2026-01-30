package app.finup.layer.domain.words.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;

/**
 * 용어사전 AI DTO 클래스
 * @author kcw
 * @since 2026-01-26
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordsAiDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Recommendation implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long termId;
        private String name;
    }


}
