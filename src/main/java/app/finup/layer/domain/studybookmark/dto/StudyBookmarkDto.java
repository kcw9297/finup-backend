package app.finup.layer.domain.studybookmark.dto;

import app.finup.layer.domain.study.dto.StudyDto;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 개념학습 북마크 DTO 클래스
 * @author kcw
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyBookmarkDto {

    /**
     * 리스트 결과로 사용
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Summary {

        private Long studyBookmarkId;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime cdate;

        private StudyDto.Summary study;
    }

    /**
     * 검색 파라미터를 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    public static class Search {

        private String order;
        private Integer pageNum;
        private Integer pageSize;

        // 빈 퍼블릭 생성자가 있으면, Jackson 라이브러리가 우선적으로 사용
        public Search() {
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