package app.finup.layer.domain.studyprogress.repository;

import app.finup.layer.domain.studyprogress.entity.StudyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

/**
 * 학습 진도 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyProgressRepository extends JpaRepository<StudyProgress, Long> {

    @Query("""
        SELECT sp
        FROM StudyProgress sp
        WHERE sp.member.memberId = :memberId
    """)
    List<StudyProgress> findByMemberId(Long memberId);


    @Modifying
    @Query("""
        DELETE FROM StudyProgress sp
        WHERE sp.member.memberId = :memberId
    """)
    void deleteByMemberId(Long memberId);

    @Modifying
    @Query("""
        DELETE FROM StudyProgress sp
        WHERE sp.study.studyId = :studyId
    """)
    void deleteByStudyId(Long studyId);

}
