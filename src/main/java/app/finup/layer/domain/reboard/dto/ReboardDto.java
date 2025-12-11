package app.finup.layer.domain.reboard.dto;

import app.finup.common.constant.Const;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 게시판 DTO 클래스
 * @author kcw
 * @since 2025-11-26
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ReboardDto {

    /**
     * 일반적인 단일(상세) 조회 결과로 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail {

        private Long idx;
        private String name;
        private String subject;
        private String content;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime regdate;
    }


    /**
     * 리스트(페이징) 결과로 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {

        private Long idx;
        private String name;
        private String subject;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = Const.ASIA_SEOUL)
        private LocalDateTime regdate;
    }


    /**
     * 검색 파라미터를 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    public static class Search {

        private String filter;
        private String keyword;
        private String order;
        private Integer pageNum;
        private Integer pageSize;

        // 빈 퍼블릭 생성자가 있으면, Jackson 라이브러리가 우선적으로 사용
        public Search() {
            this.filter = "";
            this.keyword = "";
            this.order = "latest";  // 기본 정렬 : 최신순
            this.pageNum = 0;       // 기본 페이지 : 0 (서버 기준)
            this.pageSize = 5;      // 기본 사이즈 : 5
        }

        // 만약 클라이언트에서 페이지 번호를 보낸 경우, 클라이언트에선 1부터 시작하므로 -1
        public void setPageNum(Integer pageNum) {
            this.pageNum = (Objects.isNull(pageNum) ? 1 : pageNum) - 1;
        }

        // 페이징 : OFFSET
        public int getOffset() {
            return pageNum * pageSize;
        }

        // 페이징 : LIMIT
        public int getLimit() {
            return pageSize;
        }
    }

    /**
     * 작성 요청을 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Write {

        private String name;
        private String subject;
        private String content;
    }

    /**
     * 수정 요청을 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Edit {

        private Long idx;
        private String name;
        private String subject;
        private String content;
    }

}
