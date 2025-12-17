package app.finup.layer.domain.videolink.repository;

import app.finup.layer.domain.videolink.entity.VideoLink;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDateTime;
import java.util.Collection;
import java.util.List;


/**
 * 학습용 비디오 링크 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface VideoLinkRepository extends JpaRepository<VideoLink, Long> {

    boolean existsByVideoId(String videoId);

    List<VideoLink> findByLastSyncedAtBefore(LocalDateTime threshold);

    @SuppressWarnings("SqlResolve")
    @Query(value = """
        SELECT *
        FROM video_link
        WHERE embedding IS NOT NULL
        ORDER BY VEC_DISTANCE_COSINE(embedding, :embedding)
        LIMIT :lim
    """, nativeQuery = true)
    List<VideoLink> findSimilar(byte[] embedding, int lim);


    @SuppressWarnings("SqlResolve")
    @Query(value = """
        SELECT *
        FROM video_link
        WHERE embedding IS NOT NULL AND VEC_DISTANCE_COSINE(embedding, :embedding) < :threshold
        ORDER BY VEC_DISTANCE_COSINE(embedding, :embedding)
        LIMIT :lim
    """, nativeQuery = true)
    List<VideoLink> findSimilarWithThreshold(byte[] embedding, int lim, double threshold);


    @Modifying
    @Query("""
        DELETE FROM VideoLink vl
        WHERE vl.videoId IN :videoIds
    """)
    void deleteByVideoIds(Collection<String> videoIds);
}
