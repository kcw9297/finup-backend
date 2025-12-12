package app.finup.layer.domain.videolink.dto;

import app.finup.common.utils.FormatUtils;
import app.finup.layer.domain.videolink.entity.VideoLink;
import lombok.*;

/**
 * 학습용 비디오 링크 Entity -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-04
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkDtoMapper {

   public static VideoLinkDto.Row toRow(VideoLink entity) {

       return VideoLinkDto.Row.builder()
               .videoLinkId(entity.getVideoLinkId())
               .videoId(entity.getVideoId())
               .videoUrl(entity.getVideoUrl())
               .title(entity.getTitle())
               .duration(FormatUtils.formatDuration(entity.getDuration()))
               .thumbnailUrl(entity.getThumbnailUrl())
               .channelTitle(entity.getChannelTitle())
               .publishedAt(entity.getPublishedAt())
               .viewCount(entity.getViewCount())
               .likeCount(entity.getLikeCount())
               .tags(entity.getTags().toString())
               .build();
   }

}