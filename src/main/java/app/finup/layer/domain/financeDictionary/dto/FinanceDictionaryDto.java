package app.finup.layer.domain.financeDictionary.dto;

import lombok.*;

/**
 * 용어사전 DTO 클래스
 * @author khj
 * @since 2025-12-10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FinanceDictionaryDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private Long termId;
        private String name;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static class Search {
        private String keyword;
        private Integer pageNum;
        private Integer pageSize;

        public Search() {
            this.keyword = "";
            this.pageNum = 0;
            this.pageSize = 20;
        }

        public Integer getOffset() {
            return pageNum * pageSize;
        }

        public Integer getLimit() {
            return pageSize;
        }
    }
}
