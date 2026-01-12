package app.finup.layer.domain.news.dto;

import app.finup.layer.domain.news.entity.News;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;


/**
 * DTO 매핑 기능을 지원하는 클래스
 * @author kcw
 * @since 2025-12-25
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class NewsDtoMapper {

    public static NewsDto.Row toRow(News entity) {
        return NewsDto.Row.builder()
                .title(entity.getTitle())
                .summary(entity.getSummary())
                .description(entity.getDescription())
                .thumbnail(entity.getThumbnail())
                .publisher(entity.getPublisher())
                .publishedAt(entity.getPublishedAt())
                .link(entity.getLink())
                .build();
    }
}
