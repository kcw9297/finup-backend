package app.finup.layer.domain.news.dto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsDtoMapper {

    public static NewsDto.Summary toSummary(String title, String summary, String thumbnail, String publisher, LocalDateTime publishedAt, String link) {
        return NewsDto.Summary.builder()
                .title(title)
                .summary(summary)
                .thumbnail(thumbnail)
                .publisher(publisher)
                .publishedAt(publishedAt)
                .link(link)
                .build();
    }
}
