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

    @Modifying
    @Query("""
        DELETE FROM VideoLink vl
        WHERE vl.videoId IN :videoIds
    """)
    void deleteByVideoIds(Collection<String> videoIds);
}
