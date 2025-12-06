package app.finup.layer.domain.video.dto;

import app.finup.infra.youtube.dto.YouTube;
import app.finup.layer.domain.videolink.entity.VideoLink;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * 학습용 비디오 링크 Entity -> DTO 매핑 지원 클래스
 * @author kcw
 * @since 2025-12-04
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoLinkDtoMapper {

   public static VideoLinkDto.Row toRow(VideoLink entity, YouTube.Row video) {

       return VideoLinkDto.Row.builder()
               // 엔티티에 저장된 정보
               .videoLinkId(entity.getVideoLinkId())
               .videoId(entity.getVideoId())
               .videoUrl(entity.getVideoUrl())
               .displayOrder(entity.getDisplayOrder())

               // API에서 얻은 정보
               .title(video.getTitle())
               .duration(video.get)
               .thumbnailUrl(video.)
               .channelTitle(video.)
               .viewCount(video.)
               .likeCount(video.)
               .build();
   }


    public static VideoLinkDto.Row toRow(VideoLink entity, YouTube.Row video) {

        return VideoLinkDto.Row.builder()
                // 엔티티에 저장된 정보
                .videoLinkId(entity.getVideoLinkId())
                .videoId(entity.getVideoId())
                .videoUrl(entity.getVideoUrl())
                .displayOrder(entity.getDisplayOrder())

                // API에서 얻은 정보
                .title(video.getTitle())
                .duration(video.get)
                .thumbnailUrl(video.)
                .channelTitle(video.)
                .viewCount(video.)
                .likeCount(video.)
                .build();
    }


    public static VideoLinkDto.FetchDetail toDetail(YouTube.Detail video) {



    }
}