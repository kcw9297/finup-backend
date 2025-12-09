package app.finup.layer.domain.study.dto;

import app.finup.layer.base.validation.annotation.NoSpecialText;
import app.finup.layer.base.validation.annotation.Select;
import app.finup.layer.base.validation.annotation.Text;
import jakarta.validation.constraints.NotBlank;
import lombok.*;

import java.util.Objects;

/**
 * Study(개념학습) DTO 클래스
 * @author kcw
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class StudyDto {

    /**
     * 리스트 조회 결과
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        private Long studyId;
        private String name;
        private String summary;
        private Integer level;
    }


    /**
     * 개념 상세 조회
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

        private Long studyId;
        private String name;
        private String summary;
        private String description;
        private Integer level;
    }

    /**
     * 검색 요청
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


    /**
     * 개념 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Add {

        @NoSpecialText(min = 5, max = 20)
        private String name;

        @NoSpecialText(min = 5, max = 20)
        private String summary;

        @Text(min = 10, max = 100)
        private String description;

        @Select
        private Integer level;
    }


    /**
     * 개념 추가
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Edit {

        private Long studyId;
        private String name;
        private String summary;
        private String description;
        private Integer level;
    }





}