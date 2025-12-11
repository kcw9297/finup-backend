package app.finup.layer.domain.news.dto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsDtoMapper {

    public static NewsDto.Row toRow(String title, String summary, String thumbnail, String publisher, LocalDateTime publishedAt, String link) {
        return NewsDto.Row.builder()
                .title(title)
                .summary(summary)
                .thumbnail(thumbnail)
                .publisher(publisher)
                .publishedAt(publishedAt)
                .link(link)
                .build();
    }
    public static NewsDto.Ai toAi(Map<String,Object> map) {
        NewsDto.Ai ai = new NewsDto.Ai();
        ai.setSummary((String) map.get("summary"));
        ai.setKeywords((List<Map<String,String>>) map.get("keywords"));
        ai.setInsight((String) map.get("insight"));
        return ai;
    }
}
