package app.finup.layer.domain.notice.dto;

import app.finup.layer.base.dto.SearchRequest;
import app.finup.layer.base.validation.annotation.NoSpecialText;
import app.finup.layer.base.validation.annotation.Text;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.Objects;

/**
 * 공지사항 DTO 클래스
 * @author khj
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NoticeDto {

    /**
     * 일반적인 단일(상세) 조회 결과로 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail {

        private Long noticeId;

        private String title;

        private Long viewCount;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime cdate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime udate;
    }

    /**
     * 리스트(페이징) 결과로 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {

        private Long noticeId;

        private String title;

        private String content;

        private Long viewCount;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime cdate;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime udate;
    }

    /**
     * 작성 요청을 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Write {

        @Text(min = 1, max = 30)
        private String title;

        @Text(min = 1, max = 1000)
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

        private Long noticeId;

        @Text(min = 1, max = 30)
        private String title;

        @Text(min = 1, max = 1000)
        private String content;
    }


    /**
     * 조회수 증가 로직을 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Watch {
        private Long noticeId;
        private Long increment;
    }


    /**
     * 검색을 위해 사용
     */

    @Data
    @ToString(callSuper = true)
    @EqualsAndHashCode(callSuper = false)
    public static class Search extends SearchRequest {

        private String filter = "";
        private String keyword = "";
        private String order = "latest";
    }
}
