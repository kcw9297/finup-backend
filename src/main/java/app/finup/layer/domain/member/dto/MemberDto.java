package app.finup.layer.domain.member.dto;

import app.finup.layer.domain.member.enums.MemberRole;
import app.finup.layer.domain.member.enums.MemberSocial;
import lombok.*;

import java.util.Objects;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MemberDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private Long memberId;
        private String email;
        private String nickname;
        private Boolean isActive;
        private String memberRole;
        private String socialType;
        private String socialId;
    }

    /**
     * 검색을 위해 사용
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
}
