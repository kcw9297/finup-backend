package app.finup.layer.domain.notice.repository;

import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.entity.Notice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 공지사항 게시판 레포지토리 인터페이스
 * @author khj
 * @since 2025-12-01
 */

public interface NoticeRepository extends JpaRepository<Notice, Long> {

    @Query("""
        SELECT n
        FROM Notice n
        ORDER BY n.cdate DESC
        LIMIT :lim
    """)
    List<Notice> findLatestN(Integer lim);
}
