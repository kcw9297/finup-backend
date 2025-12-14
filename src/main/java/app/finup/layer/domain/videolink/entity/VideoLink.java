package app.finup.layer.domain.videolink.entity;

import app.finup.infra.jpa.converter.DurationConverter;
import app.finup.infra.jpa.converter.StringListConverter;
import app.finup.layer.base.entity.BaseEntity;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.DynamicUpdate;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

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

    @Column(nullable = false)
    private String videoUrl;

    @Column(nullable = false)
    private String videoId; // youtube video Id 등 영상 고유 번호

    @Column(nullable = false)
    private String title;

    @Column(nullable = false)
    private String thumbnailUrl;

    @Column(nullable = false)
    private String channelTitle;

    @Convert(converter = DurationConverter.class) // 컨버터를 이용한 변환
    @Column(nullable = false)
    private Duration duration;

    @Column(nullable = false)
    private LocalDateTime publishedAt;

    @Column(nullable = false)
    private LocalDateTime lastSyncedAt; // 마지막 동기화 시간

    @Column(nullable = false)
    private Long viewCount;

    @Column(nullable = false)
    private Long likeCount;

    @Convert(converter = StringListConverter.class)
    @Column(columnDefinition = "json")
    private List<String> tags; // JSON 문자열로 저장된 태그 정보

    @Builder
    public VideoLink(String videoUrl, String videoId, String title, String thumbnailUrl, String channelTitle, Duration duration, LocalDateTime publishedAt, Long viewCount, Long likeCount, List<String> tags) {
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.channelTitle = channelTitle;
        this.duration = duration;
        this.publishedAt = publishedAt;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.tags = tags;
        setDefault();
    }

    // 초기값
    private void setDefault() {
        this.lastSyncedAt = LocalDateTime.now();
    }

    /* 엔티티 메소드 */

    /**
     * 비디오 링크 갱신 (사로운 영상으로 대체)
     * @param videoUrl 비디오 Full URL
     * @param videoId 비디오 아이디 (API 에서 얻어온 번호)
     * @param title 유튜브 영상 제목
     * @param thumbnailUrl 썸네일 이미지 주소
     * @param channelTitle 유튜브 채널명
     * @param duration 유튜브 영상 재생시간
     * @param viewCount 영상 조회수
     * @param likeCount 영상 좋아요 수
     * @param tags 비디오 태그 정보 (JSON 문자열로 DB에 저장)
     */
    public void edit(String videoUrl, String videoId, String title, String thumbnailUrl, String channelTitle, Duration duration, Long viewCount, Long likeCount, List<String> tags) {
        this.videoUrl = videoUrl;
        this.videoId = videoId;
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.channelTitle = channelTitle;
        this.duration = duration;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.tags = tags;
        this.lastSyncedAt = LocalDateTime.now();
    }


    /**
     * 비디오 링크 정보 동기회
     * @param title 유튜브 영상 제목
     * @param thumbnailUrl 썸네일 이미지 주소
     * @param channelTitle 유튜브 채널명
     * @param duration 유튜브 영상 재생시간
     * @param viewCount 영상 조회수
     * @param likeCount 영상 좋아요 수
     * @param tags 비디오 태그 정보 (JSON 문자열로 DB에 저장)
     */
    public void sync(String title, String thumbnailUrl, String channelTitle, Duration duration, Long viewCount, Long likeCount, List<String> tags) {
        this.title = title;
        this.thumbnailUrl = thumbnailUrl;
        this.channelTitle = channelTitle;
        this.duration = duration;
        this.viewCount = viewCount;
        this.likeCount = likeCount;
        this.tags = tags;
        this.lastSyncedAt = LocalDateTime.now();
    }

}


