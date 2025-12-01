package app.finup.layer.domain.news.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Summary{
        private String title;
        private String summary;
        private String thumbnail;
        private String publisher;
        private String link;
        @JsonFormat(shape = JsonFormat.Shape.STRING, pattern = "yyyy-MM-dd HH:mm:ss")
        private LocalDateTime publishedAt;
    }

    public static final List<String> ALLOWED_PRESS = List.of(
            "한국경제",
            "매일경제",
            "연합뉴스",
            "조선비즈",
            "서울경제"
    );

    public static final List<String> SubKeywords(String keyword){
        return List.of(
                keyword,
                keyword + " 전망",
                keyword + " 분석",
                keyword + " 실적",
                keyword + " 주가",
                keyword + " 증시",
                keyword + " 투자"
        );
    }

}
