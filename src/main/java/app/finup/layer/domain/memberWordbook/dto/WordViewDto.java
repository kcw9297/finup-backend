package app.finup.layer.domain.memberWordbook.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 최근 본 단어 DTO
 * @author khj
 * @since 2025-12-14
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class WordViewDto {

    /**
     * 최근 본 단어 목록 Row
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {

        private Long termId;
        private String name;
        private LocalDateTime lastViewedAt;
    }
}
