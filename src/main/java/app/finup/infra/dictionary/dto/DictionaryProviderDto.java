package app.finup.infra.dictionary.dto;


import lombok.*;

/**
 * 용어 사전 Provider 전용 DTO
 * @author khj
 * @since 2025-12-10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class DictionaryProviderDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private String name;
        private String description;
    }
}
