package app.finup.layer.domain.news.dto;

import app.finup.api.external.news.dto.NewsApi;
import app.finup.layer.domain.news.support.NewsObject;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 뉴스 정보를 담기 위한 DTO 클래스
 * @author kcw
 * @since 2025-12-25
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row implements Serializable, NewsObject {

        @Serial
        private static final long serialVersionUID = 1L;

        private Long newsId;
        private String title;
        private String summary;
        private String description;
        private String thumbnail;
        private String publisher;
        private String link;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime publishedAt;
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class CrawlResult {

        private String description;
        private String thumbnail;
        private String publisher;
        private boolean success;

        public static CrawlResult success(String description, String thumbnail, String publisher) {
            return CrawlResult.builder()
                    .description(description)
                    .thumbnail(thumbnail)
                    .publisher(publisher)
                    .success(true)
                    .build();
        }


        public static CrawlResult fail() {
            return CrawlResult.builder()
                    .description("")
                    .thumbnail("")
                    .publisher("")
                    .success(false)
                    .build();
        }
    }
}
