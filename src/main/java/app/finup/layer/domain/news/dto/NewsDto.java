package app.finup.layer.domain.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

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
    public static class Row implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String title;
        private String summary;
        private String description;
        private String thumbnail;
        private String publisher;
        private String link;

        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime publishedAt;
    }
}
