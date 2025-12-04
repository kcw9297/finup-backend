package app.finup.layer.domain.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;
import java.util.List;

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
    }

    public static final List<String> ALLOWED_PRESS = List.of(
            "한국경제",
            "매일경제",
            "연합뉴스",
            "조선비즈",
            "서울경제",
            "머니투데이",
            "아시아경제",
            "뉴시스",
            "파이낸셜뉴스",
            "디지털타임스",
            "전자신문",
            "헤럴드경제"
    );

}
