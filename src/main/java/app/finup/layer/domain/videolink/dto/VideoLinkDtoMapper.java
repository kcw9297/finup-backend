package app.finup.layer.domain.videolink.dto;

import app.finup.common.utils.FormatUtils;
import app.finup.infra.youtube.dto.YouTube;
import app.finup.layer.domain.videolink.entity.VideoLink;
import lombok.*;

/**
 * 학습용 비디오 링크 Entity -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-04
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkDtoMapper {

   public static VideoLinkDto.Row toRow(VideoLink entity, YouTube.Detail video) {

       return VideoLinkDto.Row.builder()

               // 엔티티에 저장된 정보
               .videoLinkId(entity.getVideoLinkId())
               .videoId(entity.getVideoId())
               .videoUrl(entity.getVideoUrl())


               // API에서 얻은 정보
               .title(video.getTitle())
               .duration(FormatUtils.formatDuration(video.getDuration()))
               .thumbnailUrl(video.getThumbnailUrl())
               .channelTitle(video.getChannelTitle())
               .viewCount(video.getViewCount())
               .likeCount(video.getLikeCount())
               .build();
   }

}