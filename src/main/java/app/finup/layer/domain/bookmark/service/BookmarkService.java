package app.finup.layer.domain.bookmark.service;

import app.finup.layer.domain.bookmark.dto.BookmarkDto;

/**
 * 북마크 로직처리 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface BookmarkService {

    void bookmark(BookmarkDto.Bookmark rq);

    void unbookmark(Long bookmarkId);
}
