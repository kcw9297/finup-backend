package app.finup.layer.domain.videolink.dto;

import app.finup.common.utils.FormatUtils;
import app.finup.layer.domain.study.entity.Study;
import app.finup.layer.domain.videolink.entity.VideoLink;
import lombok.*;

import java.util.Collection;
import java.util.List;
import java.util.Objects;

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
               .tags(Objects.isNull(entity.getTags()) ? null : entity.getTags().toString())
               .build();
   }


    public static VideoLinkDto.Recommendation toRecommendation(Study entity, Collection<VideoLinkDto.Row> candidates, List<Long> latestVideoLinkIds) {

       // [1] 학습 데이터
        VideoLinkDto.Recommendation.StudyInfo study =
                VideoLinkDto.Recommendation.StudyInfo.builder()
                        .name(entity.getName())
                        .summary(entity.getSummary())
                        .detail(entity.getDetail())
                        .level(entity.getLevel())
                        .build();

        // [2] 영상 데이터
        List<VideoLinkDto.Recommendation.VideoCandidate> videoCandidates =
                candidates.stream()
                        .map(candidate -> VideoLinkDto.Recommendation.VideoCandidate.builder()
                                .videoLinkId(candidate.getVideoLinkId())
                                .title(candidate.getTitle())
                                .channelTitle(candidate.getChannelTitle())
                                .description(candidate.getDescription())
                                .tags(candidate.getTags())
                                .build()
                        ).toList();

        // [3] DTO 반환
        return VideoLinkDto.Recommendation.builder()
                .study(study)
                .candidates(videoCandidates)
                .latestVideoLinkIds(latestVideoLinkIds)
                .build();
    }
}