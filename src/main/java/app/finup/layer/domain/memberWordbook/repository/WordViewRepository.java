package app.finup.layer.domain.memberWordbook.repository;

import app.finup.layer.domain.memberWordbook.dto.WordViewDto;
import app.finup.layer.domain.memberWordbook.entity.WordView;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

/**
 * 최근 본 단어 Repository
 * @author khj
 * @since 2025-12-14
 */
public interface WordViewRepository extends JpaRepository<WordView, Long> {


    /**
     * 특정 회원이 특정 단어를 이미 조회한 기록이 있는지 조회
     *
     */

    @Query("""
        SELECT wv
        FROM WordView wv
        WHERE wv.memberId = :memberId
          AND wv.termId = :termId
    """)
    Optional<WordView> findByMemberAndTerm(
            @Param("memberId") Long memberId,
            @Param("termId") Long termId
    );

    /**
     * 특정 회원의 최근 본 단어 목록을 DTO 형태로 조회
     * 화면 응답, API 반환 전용 메소드
     */

    @Query("""
        SELECT new app.finup.layer.domain.memberWordbook.dto.WordViewDto.Row(
            wv.termId,
            w.name,
            wv.lastViewedAt
        )
        FROM WordView wv
        JOIN Words w ON w.termId = wv.termId
        WHERE wv.memberId = :memberId
        ORDER BY wv.lastViewedAt DESC
    """)

    List<WordViewDto.Row> findRecentRows(@Param("memberId") Long memberId);

}
