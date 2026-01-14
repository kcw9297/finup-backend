package app.finup.layer.domain.video.dto;

import app.finup.common.utils.FormatUtils;
import app.finup.api.external.youtube.dto.YouTubeApiDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Youtube API DTO -> 반환용 Video DTO 로 변환을 지원하는 매퍼 클래스
 * @author kcw
 * @since 2025-12-07
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class VideoDtoMapper {

   public static VideoDto.Detail toDetail(YouTubeApiDto.Detail video) {

       return VideoDto.Detail.builder()
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


    public static VideoDto.Row toRow(YouTubeApiDto.Row video) {

        return VideoDto.Row.builder()
                .title(video.getTitle())
                .videoUrl(video.getVideoUrl())
                .thumbnailUrl(video.getThumbnailUrl())
                .channelTitle(video.getChannelTitle())
                //.comment("") // AI 분석
                //.recommendationLevel("") // AI 추천도
                .build();
    }
}