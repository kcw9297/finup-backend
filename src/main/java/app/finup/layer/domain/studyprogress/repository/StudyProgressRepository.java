package app.finup.layer.domain.studyprogress.repository;

import app.finup.layer.domain.studyprogress.entity.StudyProgress;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

/**
 * 학습 진도 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
@Transactional(readOnly = true)
public interface StudyProgressRepository extends JpaRepository<StudyProgress, Long> {

    @Query("""
        SELECT sp
        FROM StudyProgress sp
        WHERE sp.member.memberId = :memberId
    """)
    List<StudyProgress> findByMemberId(Long memberId);


    @Query("""
        SELECT sp
        FROM StudyProgress sp
        WHERE sp.study.studyId = :studyId AND sp.member.memberId = :memberId
    """)
    Optional<StudyProgress> findByStudyIdAndMemberId(Long studyId, Long memberId);


    @Transactional
    @Modifying
    @Query("""
        DELETE FROM StudyProgress sp
        WHERE sp.member.memberId = :memberId
    """)
    void deleteByMemberId(Long memberId);


    @Transactional
    @Modifying
    @Query("""
        DELETE FROM StudyProgress sp
        WHERE sp.study.studyId = :studyId
    """)
    void deleteByStudyId(Long studyId);


    @Transactional
    @Modifying
    @Query("""
        DELETE FROM StudyProgress sp
        WHERE sp.study.studyId = :studyId AND sp.member.memberId = :memberId
    """)
    void deleteByStudyIdAndMemberId(Long studyId, Long memberId);

}
