package app.finup.layer.domain.level.repository;

import app.finup.layer.domain.level.entity.UserLevelProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;
import java.util.List;

/**
 * UserLevelProgress 엔티티 Repository 인터페이스
 * @author sjs
 * @since 2025-12-05
 */
public interface UserLevelProgressRepository extends JpaRepository<UserLevelProgress, Long> {

    /**
     * 회원의 전체 단계 진도 조회
     */
    @Query("""
        SELECT p
        FROM UserLevelProgress p
        WHERE p.member.memberId = :memberId
    """)
    List<UserLevelProgress> findByMemberId(Long memberId);

    /**
     * 특정 회원의 특정 단계 진도 조회
     */
    @Query("""
        SELECT p
        FROM UserLevelProgress p
        WHERE p.member.memberId = :memberId
        AND p.level.levelId = :levelId
    """)
    Optional<UserLevelProgress> findByMemberIdAndLevelId(Long memberId, Long levelId);
}
