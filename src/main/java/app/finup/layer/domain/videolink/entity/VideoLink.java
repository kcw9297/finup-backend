package app.finup.layer.domain.videolink.entity;

import app.finup.layer.base.entity.BaseEntity;
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
public class VideoLink extends BaseEntity {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long videoLinkId;

    @Column(nullable = false, updatable = false)
    private Long ownerId; // 소유주는 갱신 불가

    @Enumerated(value = EnumType.STRING)
    @Column(nullable = false, updatable = false)
    private VideoLinkOwner videoLinkOwner; // 소유주는 갱신 불가

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private Long videoId; // youtube video Id 등 영상 고유 번호

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private Double displayOrder;  // 정렬 순서

    @Builder
    public VideoLink(Long ownerId, VideoLinkOwner videoLinkOwner, String videoUrl, Long videoId, String thumbnailUrl, String title, Double displayOrder) {
        this.ownerId = ownerId;
        this.videoLinkOwner = videoLinkOwner;
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
        this.displayOrder = displayOrder;
    }

    /* 엔티티 메소드 */

    /**
     * 재정렬 로직
     * @param displayOrder 정렬 순서
     */
    public void reorder(Double displayOrder) {
        this.displayOrder = displayOrder;
    }

    /**
     * 비디오 링크정보 갱신
     * @param videoUrl 비디오 Full URL
     * @param videoId 비디오 아이디 (API 에서 얻어온 번호)
     * @param thumbnailUrl API에서 제공받은 썸네일 이미지 URL
     * @param title API에서 제공받은 비디오 제목
     */
    public void update(String videoUrl, Long videoId, String thumbnailUrl, String title) {
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.thumbnailUrl = thumbnailUrl;
        this.title = title;
    }
}


