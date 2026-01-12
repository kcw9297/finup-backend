package app.finup.infra.api.news.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * YouTube API 로부터 얻어온 데이터를 담는 DTO 클래스
 * @author kcw
 * @since 2025-12-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsApi {

    /**
     * 뉴스 API 응답을 담을 DTO
     */
    @Getter
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class SearchRp implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        // 검색 결과 (key : items)
        private List<Item> items; // 단일 조회인 경우 길이가 1인 응답으로 제공

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {
            private String title; // 뉴스 제목 (HTML 태그 포함)
            private String description; // 뉴스 요약 (HTML 태그 포함)
            private String link; // 뉴스 링크
            private String pubDate; // 발행일 (RFC 1123 형식: "Mon, 23 Dec 2024 10:00:00 +0900")
        }
    }

    /**
     * 뉴스 목록을 담을 DTO (API 응답 가공)
     */
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String title;
        private String summary;
        private String link;
        private LocalDateTime publishedAt;
    }

}