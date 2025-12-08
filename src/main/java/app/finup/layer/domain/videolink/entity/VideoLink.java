package app.finup.layer.domain.videolink.entity;

import app.finup.layer.base.entity.BaseEntity;
import app.finup.layer.base.inter.Reorderable;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.member.entity.Member;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;
import org.hibernate.annotations.OnDelete;
import org.hibernate.annotations.OnDeleteAction;

/**
 * 학습용 영상 링크 엔티티 클래스
 * @author kcw
 * @since 2025-12-02
 */

@Entity
@Table(name = "video_link")
@DynamicUpdate
@Getter
@ToString(callSuper = true)
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
public class VideoLink extends BaseEntity implements Reorderable {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoLinkId;

    @Column(updatable = false) // HOME이 소유주인 경우 null
    private Long ownerId; // 소유주는 갱신 불가

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private VideoLinkOwner videoLinkOwner; // 소유주는 갱신 불가

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String videoId; // youtube video Id 등 영상 고유 번호

    @Column(nullable = false)
    private Double displayOrder;  // 정렬 순서

    @Builder
    public VideoLink(Long ownerId, VideoLinkOwner videoLinkOwner, String videoUrl, String videoId, Double displayOrder) {
        this.ownerId = ownerId;
        this.videoLinkOwner = videoLinkOwner;
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.displayOrder = displayOrder;
    }

    /* 엔티티 메소드 */

    /**
     * 비디오 링크정보 갱신
     * @param videoUrl 비디오 Full URL
     * @param videoId 비디오 아이디 (API 에서 얻어온 번호)
     */
    public void edit(String videoUrl, String videoId) {
        this.videoUrl = videoUrl;
        this.videoId = videoId;
    }

    // 정렬 순서 변경
    @Override
    public void reorder(Double displayOrder) {
        this.displayOrder = displayOrder;
    }
}


