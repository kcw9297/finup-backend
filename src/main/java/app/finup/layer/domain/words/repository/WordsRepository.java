package app.finup.layer.domain.words.repository;

import app.finup.layer.domain.words.entity.Words;
import app.finup.layer.domain.words.enums.WordsLevel;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

public interface WordsRepository extends JpaRepository<Words, Long> {
    Optional<Words> findByName(@Param("name") String name);

    long count();

    /**
     * 퀴즈용 랜덤 단어 1개 조회
     */
    @Query("""
        SELECT w
        FROM Words w
        ORDER BY function('RAND')
    """)
    List<Words> findRandom(Pageable pageable);


    /**
     * 단어 ID 기반으로 가져오는 거
     */
    Optional<Words> findByTermId(Long termId);

    /**
     * 퀴즈 오답 후보용 단어명 조회 (정답 제외)
     */
    @Query("""
        SELECT w.name
            FROM Words w
            WHERE w.termId <> :termId
            ORDER BY function('RAND')
    """)
    List<String> findRandomNamesExclude(
            @Param("termId") Long termId,
            Pageable pageable
    );// 전체 건수 (isInitialized 용)

    @Query("select w from Words w order by w.termId asc")
    List<Words> findTop10(Pageable pageable);


    @Query("""
        SELECT w
        FROM Words w
        WHERE
            w.wordsLevel = :wordsLevel AND
            w.termId NOT IN :excludeIds
        ORDER BY FUNCTION('RAND')
        LIMIT :lim
    """)
    List<Words> findRandomByWordLevelWithExcludeIds(WordsLevel wordsLevel, Collection<Long> excludeIds, int lim);
}
