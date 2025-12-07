package app.finup.layer.domain.bookmark.dto;

import app.finup.layer.domain.bookmark.entity.Bookmark;
import lombok.*;


/**
 * Bookmark Entity -> DTO 매퍼 클래스
 * @author kcw
 * @since 2025-12-07
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class BookmarkDtoMapper {

    public static BookmarkDto.Row toRow(Bookmark entity) {

        return BookmarkDto.Row.builder()
                .bookmarkId(entity.getBookmarkId())
                .targetId(entity.getTargetId())
                .bookmarkTarget(entity.getBookmarkTarget().name())
                .build();
    }
}