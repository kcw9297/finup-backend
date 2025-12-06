package app.finup.layer.domain.video.dto;

import app.finup.common.utils.FormatUtils;
import app.finup.infra.youtube.dto.YouTube;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Youtube API DTO -> 반환용 Video DTO 로 변환을 지원하는 매퍼 클래스
 * @author kcw
 * @since 2025-12-07
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoDtoMapper {

   public static VideoDto.DetailRow toDetailRow(YouTube.Detail video) {

       return VideoDto.DetailRow.builder()
               .videoId(video.getVideoId())
               .videoUrl(video.getVideoUrl())
               .title(video.getTitle())
               .duration(FormatUtils.formatDuration(video.getDuration()))
               .thumbnailUrl(video.getThumbnailUrl())
               .channelTitle(video.getChannelTitle())
               .viewCount(video.getViewCount())
               .likeCount(video.getLikeCount())
               .build();
   }


    public static VideoDto.SearchRow toSearchRow(YouTube.Row video) {

        return VideoDto.SearchRow.builder()
                .title(video.getTitle())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .channelTitle(video.getTitle())
                //.comment("") // AI 분석
                //.recommendationLevel("") // AI 추천도
                .build();
    }
}