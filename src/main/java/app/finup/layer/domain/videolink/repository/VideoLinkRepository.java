package app.finup.layer.domain.videolink.repository;

import aj.org.objectweb.asm.commons.Remapper;
import app.finup.layer.domain.videolink.entity.VideoLink;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;
import java.util.Optional;

/**
 * 학습용 비디오 링크 엔티티 JPA 레포지토리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface VideoLinkRepository extends JpaRepository<VideoLink, Long> {

    @Query("""
        SELECT vl
        FROM VideoLink vl
        WHERE vl.videoLinkOwner = :videoLinkOwner
        ORDER BY vl.displayOrder
    """)
    List<VideoLink> findByVideoLinkOwner(VideoLinkOwner videoLinkOwner);


    @Query("""
        SELECT vl
        FROM VideoLink vl
        WHERE vl.videoLinkOwner = :videoLinkOwner
        ORDER BY vl.displayOrder DESC
        LIMIT 1
    """)
    Optional<VideoLink> findLastByVideoLinkOwner(VideoLinkOwner videoLinkOwner);


    @Query("""
        SELECT vl
        FROM VideoLink vl
        WHERE vl.ownerId = :ownerId AND vl.videoLinkOwner = :videoLinkOwner
        ORDER BY vl.displayOrder DESC
        LIMIT 1
    """)
    Optional<VideoLink> findLastByOwnerIdAndVideoLinkOwner(Long ownerId, VideoLinkOwner videoLinkOwner);


    Boolean existsByVideoId(String videoId);


    @Query("""
        SELECT vl
        FROM VideoLink vl
        WHERE vl.ownerId = :ownerId AND vl.videoLinkOwner = :videoLinkOwner
        ORDER BY vl.displayOrder
    """)
    List<VideoLink> findByOwnerIdAndVideoLinkOwner(Long ownerId, VideoLinkOwner videoLinkOwner);


    @Modifying // 변경 로직
    @Query("""
        DELETE FROM VideoLink vl
        WHERE vl.ownerId = :ownerId
    """)
    void deleteByOwnerId(Long ownerId);
}
