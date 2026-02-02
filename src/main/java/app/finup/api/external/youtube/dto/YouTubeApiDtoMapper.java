package app.finup.api.external.youtube.dto;

import app.finup.api.external.youtube.utils.YouTubeUtils;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * YouTube API DTO 간 변환을 지원하는 클래스
 * @author kcw
 * @since 2025-12-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YouTubeApiDtoMapper {

    public static YouTubeApiDto.Detail toDetail(YouTubeApiDto.VideosRp rp, String videoId) {

        // [1] items 추출 - 상세 조회시엔 0번째 인덱스에 상세 정보가 있음
        YouTubeApiDto.VideosRp.Item item = rp.getItems().get(0);

        // [2] items 내 상세 정보 추출 및 변환
        return extractItemInfoAndMapToDetail(item, videoId);
    }


    // "id"를 여러개로 detail을 검색한 경우
    public static List<YouTubeApiDto.Detail> toDetails(YouTubeApiDto.VideosRp rp, List<String> videoIds) {

        // [1] items 추출 - 상세 조회시엔 0번째 인덱스에 상세 정보가 있음
        Map<String, YouTubeApiDto.VideosRp.Item> itemsMap =
                rp.getItems()
                        .stream()
                        .collect(Collectors.toConcurrentMap(
                                YouTubeApiDto.VideosRp.Item::getId,
                                Function.identity()
                        ));

        // [2] DTO 일괄 변환 및 반환 (item 내 정보 기반)
        return videoIds.stream()
                .filter(videoId -> Objects.nonNull(itemsMap.get(videoId))) // 영상이 정상 존재하는 것만 추출
                .map(videoId -> extractItemInfoAndMapToDetail(itemsMap.get(videoId), videoId)) // DTO 변환
                .toList();
    }


    // item 내부에서, 필요 정보를 추출하고
    private static YouTubeApiDto.Detail extractItemInfoAndMapToDetail(
            YouTubeApiDto.VideosRp.Item item,
            String videoId
    ) {

        // snippet
        YouTubeApiDto.VideosRp.Item.Snippet snippet = item.getSnippet();
        YouTubeApiDto.VideosRp.Item.Snippet.Thumbnails thumbnails =
                snippet != null ? snippet.getThumbnails() : null;

        YouTubeApiDto.VideosRp.Item.Snippet.Thumbnails.Thumbnail standard =
                thumbnails != null ? thumbnails.getStandard() : null;

        // contentDetails
        YouTubeApiDto.VideosRp.Item.ContentDetails contentDetails = item.getContentDetails();

        // statistics
        YouTubeApiDto.VideosRp.Item.Statistics statistics = item.getStatistics();

        // publishedAt (null + blank 보호)
        Instant publishedAt = null;
        if (snippet != null && snippet.getPublishedAt() != null && !snippet.getPublishedAt().isBlank()) {
            publishedAt = Instant.parse(snippet.getPublishedAt());
        }

        // duration (null + blank 보호)
        Duration duration = null;
        if (contentDetails != null && contentDetails.getDuration() != null
                && !contentDetails.getDuration().isBlank()) {
            duration = Duration.parse(contentDetails.getDuration());
        }

        // viewCount (null + blank 보호)
        Long viewCount = 0L;
        if (statistics != null && statistics.getViewCount() != null
                && !statistics.getViewCount().isBlank()) {
            viewCount = Long.valueOf(statistics.getViewCount());
        }

        // likeCount (null + blank 보호)
        Long likeCount = 0L;
        if (statistics != null && statistics.getLikeCount() != null
                && !statistics.getLikeCount().isBlank()) {
            likeCount = Long.valueOf(statistics.getLikeCount());
        }

        // thumbnailUrl
        String thumbnailUrl = standard != null ? standard.getUrl() : null;

        return YouTubeApiDto.Detail.builder()
                .videoUrl(YouTubeUtils.toVideoUrl(videoId))
                .videoId(item.getId())
                .publishedAt(publishedAt)
                .title(snippet != null ? snippet.getTitle() : null)
                .description(snippet != null ? snippet.getDescription() : null)
                .channelTitle(snippet != null ? snippet.getChannelTitle() : null)
                .tags(snippet != null ? snippet.getTags() : null)
                .thumbnailUrl(thumbnailUrl)
                .duration(duration)
                .viewCount(viewCount)
                .likeCount(likeCount)
                .build();
    }





    public static List<YouTubeApiDto.Row> toRows(YouTubeApiDto.SearchRp rp) {

        // [1] items 추출 (검색 결과)
        List<YouTubeApiDto.SearchRp.Item> items = rp.getItems();

        // [2] 검색 결과 매핑 및 밴환
        return items.stream()
                .map(YouTubeApiDtoMapper::mapToRow)
                .toList();
    }

    // 각 검색 결과 매핑
    private static YouTubeApiDto.Row mapToRow(YouTubeApiDto.SearchRp.Item item) {

        // [1] item 내 상세 정보 추출
        YouTubeApiDto.SearchRp.Item.Id id = item.getId();

        // snippet
        YouTubeApiDto.SearchRp.Item.Snippet snippet = item.getSnippet();
        YouTubeApiDto.SearchRp.Item.Snippet.Thumbnails thumbnails = snippet.getThumbnails();
        YouTubeApiDto.SearchRp.Item.Snippet.Thumbnails.Thumbnail high = thumbnails.getHigh();

        // [2] DTO 변환 및 반환
        return YouTubeApiDto.Row.builder()
                .videoId(id.getVideoId())
                .videoUrl(YouTubeUtils.toVideoUrl(id.getVideoId()))
                .thumbnailUrl(high.getUrl())
                .title(snippet.getTitle())
                .build();
    }

}