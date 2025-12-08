package app.finup.infra.youtube.dto;

import app.finup.infra.youtube.utils.YouTubeUtils;
import lombok.*;

import java.time.Duration;
import java.time.Instant;
import java.util.*;
import java.util.concurrent.ConcurrentMap;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * YouTube API DTO 간 변환을 지원하는 클래스
 * @author kcw
 * @since 2025-12-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YouTubeDtoMapper {

    public static YouTube.Detail toDetail(YouTube.VideosRp rp, String videoId) {

        // [1] items 추출 - 상세 조회시엔 0번째 인덱스에 상세 정보가 있음
        YouTube.VideosRp.Item item = rp.getItems().get(0);

        // [2] items 내 상세 정보 추출 및 변환
        return extractItemInfoAndMapToDetail(item, videoId);
    }


    // "id"를 여러개로 detail을 검색한 경우
    public static List<YouTube.Detail> toDetails(YouTube.VideosRp rp, List<String> videoIds) {

        // [1] items 추출 - 상세 조회시엔 0번째 인덱스에 상세 정보가 있음
        Map<String, YouTube.VideosRp.Item> itemsMap =
                rp.getItems()
                        .stream()
                        .collect(Collectors.toConcurrentMap(
                                YouTube.VideosRp.Item::getId,
                                Function.identity()
                        ));

        // [2] DTO 일괄 변환 및 반환 (item 내 정보 기반)
        return videoIds.stream()
                .filter(videoId -> Objects.nonNull(itemsMap.get(videoId))) // 영상이 정상 존재하는 것만 추출
                .map(videoId -> extractItemInfoAndMapToDetail(itemsMap.get(videoId), videoId)) // DTO 변환
                .toList();
    }


    // item 내부에서, 필요 정보를 추출하고
    private static YouTube.Detail extractItemInfoAndMapToDetail(YouTube.VideosRp.Item item, String videoId) {

        // snippet
        YouTube.VideosRp.Item.Snippet snippet = item.getSnippet();
        YouTube.VideosRp.Item.Snippet.Thumbnails thumbnails = snippet.getThumbnails();
        YouTube.VideosRp.Item.Snippet.Thumbnails.Thumbnail standard = thumbnails.getStandard();

        // contentDetails
        YouTube.VideosRp.Item.ContentDetails contentDetails = item.getContentDetails();

        // statistics
        YouTube.VideosRp.Item.Statistics statistics = item.getStatistics();

        // [3] 응답 데이터 변환
        return YouTube.Detail.builder()
                .videoUrl(YouTubeUtils.toVideoUrl(videoId))
                .videoId(item.getId())
                .publishedAt(Instant.parse(snippet.getPublishedAt()))
                .title(snippet.getTitle())
                .description(snippet.getDescription())
                .channelTitle(snippet.getChannelTitle())
                .tags(snippet.getTags())
                .thumbnailUrl(standard.getUrl())
                .duration(Duration.parse(contentDetails.getDuration()))
                .viewCount(Long.valueOf(statistics.getViewCount()))
                .likeCount(Long.valueOf(statistics.getLikeCount()))
                .build();
    }




    public static List<YouTube.Row> toRows(YouTube.SearchRp rp) {

        // [1] items 추출 (검색 결과)
        List<YouTube.SearchRp.Item> items = rp.getItems();

        // [2] 검색 결과 매핑 및 밴환
        return items.stream()
                .map(YouTubeDtoMapper::mapToRow)
                .toList();
    }

    // 각 검색 결과 매핑
    private static YouTube.Row mapToRow(YouTube.SearchRp.Item item) {

        // [1] item 내 상세 정보 추출
        YouTube.SearchRp.Item.Id id = item.getId();

        // snippet
        YouTube.SearchRp.Item.Snippet snippet = item.getSnippet();
        YouTube.SearchRp.Item.Snippet.Thumbnails thumbnails = snippet.getThumbnails();
        YouTube.SearchRp.Item.Snippet.Thumbnails.Thumbnail high = thumbnails.getHigh();

        // [2] DTO 변환 및 반환
        return YouTube.Row.builder()
                .videoId(id.getVideoId())
                .videoUrl(YouTubeUtils.toVideoUrl(id.getVideoId()))
                .thumbnailUrl(high.getUrl())
                .title(snippet.getTitle())
                .build();
    }

}