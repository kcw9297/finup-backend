package app.finup.layer.domain.words.dto;

import lombok.*;

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
    public static class Row {
        private Long termId;
        private String name;
        private String description;
    }

    @Data
    @AllArgsConstructor
    public static class Search {
        private Integer pageNum;
        private Integer pageSize;

        private String keyword;
        private String filter;
        private String order;


        public Search() {
            this.keyword = "";
            this.pageNum = 0;
            this.pageSize = 20;
            this.filter = "";
            this.order = "latest";
        }

        public Integer getOffset() {
            return pageNum * pageSize;
        }

        public Integer getLimit() {
            return pageSize;
        }
    }
}
