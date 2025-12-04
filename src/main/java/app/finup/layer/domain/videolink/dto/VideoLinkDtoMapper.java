package app.finup.layer.domain.videolink.dto;

import app.finup.layer.domain.videolink.entity.VideoLink;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
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
               .thumbnailUrl(entity.getThumbnailUrl())
               .title(entity.getTitle())
               .displayOrder(entity.getDisplayOrder())
               .build();
   }
}