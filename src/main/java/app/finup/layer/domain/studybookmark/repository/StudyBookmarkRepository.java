package app.finup.layer.domain.studybookmark.repository;

import app.finup.layer.domain.studybookmark.entity.StudyBookmark;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 단계별 학습 북마크 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyBookmarkRepository extends JpaRepository<StudyBookmark, Long> {

    @Query("""
        SELECT sb
        FROM StudyBookmark sb
        LEFT JOIN FETCH sb.member
        WHERE sb.member.memberId = :memberId
    """)
    List<StudyBookmark> findWithMemberByMemberId(Long memberId);
}
