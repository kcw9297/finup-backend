package app.finup.layer.domain.words.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 용어사전 DTO 클래스
 * @author khj
 * @since 2025-12-10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordsDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long termId;
        private String name;
        private String description;
    }


}
