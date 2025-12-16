package app.finup.layer.domain.news.dto;
import app.finup.layer.domain.news.enums.AiType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsDtoMapper {

    public static NewsDto.Row toRow(String title, String description, String thumbnail, String publisher, LocalDateTime publishedAt, String link) {
        return NewsDto.Row.builder()
                .title(title)
                .description(description)
                .thumbnail(thumbnail)
                .publisher(publisher)
                .publishedAt(publishedAt)
                .link(link)
                .build();
    }
    public static NewsDto.Ai toAi(Map<String,Object> map) {
        NewsDto.Ai ai = new NewsDto.Ai();
        ai.setType(AiType.DEEP);
        ai.setSummary((String) map.get("summary"));
        ai.setKeywords((List<Map<String,String>>) map.get("keywords"));
        ai.setInsight((String) map.get("insight"));
        return ai;
    }

    public static NewsDto.Summary toSummary(Map<String,Object> map) {
        NewsDto.Summary sum = new NewsDto.Summary();
        sum.setType(AiType.LIGHT);
        sum.setSummary((String) map.get("summary"));
        sum.setKeywords((List<Map<String,String>>) map.get("keywords"));
        return sum;
    }
}
