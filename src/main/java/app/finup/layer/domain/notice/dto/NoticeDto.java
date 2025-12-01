package app.finup.layer.domain.notice.dto;

import lombok.*;

import java.time.LocalDateTime;

/**
 * 공지사항 DTO 클래스
 * @author khj
 * @since 2025-12-01
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NoticeDto {

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
        private String content;
        private String admin;
        private LocalDateTime cdate;
        private LocalDateTime udate;
    }

    /**
     * 리스트(페이징) 결과로 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class NoticeList {
        private Long noticeId;
        private String title;
        private String content;
        private String admin;
        private LocalDateTime cdate;
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
        private String title;
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
        private String title;
        private String content;
    }
}
