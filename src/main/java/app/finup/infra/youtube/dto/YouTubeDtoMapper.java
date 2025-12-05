package app.finup.infra.youtube.dto;

import app.finup.infra.youtube.utils.YouTubeUtils;
import lombok.*;

import java.util.List;

/**
 * YouTube API DTO 간 변환을 지원하는 클래스
 * @author kcw
 * @since 2025-12-05
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class YouTubeDtoMapper {

    public static YouTube.Detail toDetail(YouTube.VideosRp rp, String videoUrl) {

        // [1] items 추출 - 상세 조회시엔 0번째 인덱스에 상세 정보가 있음
        YouTube.VideosRp.Item item = rp.getItems().get(0);

        // [2] items 내 상세 정보 추출
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
                .videoUrl(videoUrl)
                .videoId(item.getId())
                .title(snippet.getTitle())
                .channelTitle(snippet.getChannelTitle())
                .thumbnailUrl(standard.getUrl())
                .duration(contentDetails.getDuration())
                .viewCount(statistics.getViewCount())
                .likeCount(statistics.getLikeCount())
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
        // id
        YouTube.SearchRp.Item.Id id = item.getId();

        // snippet
        YouTube.SearchRp.Item.Snippet snippet = item.getSnippet();
        YouTube.SearchRp.Item.Snippet.Thumbnails thumbnails = snippet.getThumbnails();
        YouTube.SearchRp.Item.Snippet.Thumbnails.Thumbnail high = thumbnails.getHigh();


        return YouTube.Row.builder()
                .videoId(id.getVideoId())
                .videoUrl(YouTubeUtils.getVideoUrl(id.getVideoId()))
                .thumbnailUrl(high.getUrl())
                .title(snippet.getTitle())
                .build();
    }


}