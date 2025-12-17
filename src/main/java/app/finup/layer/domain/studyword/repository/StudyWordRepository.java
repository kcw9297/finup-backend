package app.finup.layer.domain.studyword.repository;

import app.finup.layer.domain.studyword.entity.StudyWord;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;


/**
 * 단계별 학습 단어 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyWordRepository extends JpaRepository<StudyWord, Long> {

    @Query("""
        SELECT sw
        FROM StudyWord sw
        LEFT JOIN FETCH sw.wordImageFile
        WHERE sw.studyWordId = :studyWordId
    """)
    Optional<StudyWord> findWithImageById(Long studyWordId);


    @Query("""
        SELECT CASE WHEN COUNT(sw) > 0 THEN true ELSE false END
        FROM StudyWord sw
        WHERE REPLACE(sw.name, ' ', '') = REPLACE(:name, ' ', '')
    """)
    boolean existsByNameIgnoreSpaces(String name);


    @Query("""
        select sw from StudyWord sw
        order by function('rand')
    """)
    List<StudyWord> findRandomWords(Pageable pageable);
}
