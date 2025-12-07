package app.finup.layer.domain.bookmark.repository;

import app.finup.layer.domain.bookmark.entity.Bookmark;
import app.finup.layer.domain.bookmark.enums.BookmarkTarget;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 북마크 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface BookmarkRepository extends JpaRepository<Bookmark, Long> {

    @Query("""
        SELECT b
        FROM Bookmark b
        WHERE b.member.memberId = :memberId
    """)
    List<Bookmark> findByMemberId(Long memberId);


    @Modifying
    @Query("""
        DELETE FROM Bookmark b
        WHERE b.member.memberId = :memberId
    """)
    void deleteByMemberId(Long memberId);


    @Modifying
    @Query("""
        DELETE FROM Bookmark b
        WHERE b.targetId = :targetId AND b.bookmarkTarget = :bookmarkTarget
    """)
    void deleteByTargetIdAndBookmarkTarget(Long targetId, BookmarkTarget bookmarkTarget);
}
