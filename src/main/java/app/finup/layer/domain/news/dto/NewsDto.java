package app.finup.layer.domain.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

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
        private String thumbnail;
        private String publisher;
        private String link;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss", timezone = "Asia/Seoul")
        private LocalDateTime publishedAt;
        private Ai ai;
    }
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Ai implements Serializable{
        @Serial
        private static final long serialVersionUID = 1L;

        private String summary;
        private List<Map<String,String>> keywords;
        private String insight;
    }
}
