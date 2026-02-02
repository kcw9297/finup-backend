package app.finup.layer.base.template;

import app.finup.api.external.youtube.dto.YouTubeApiDto;
import app.finup.api.external.youtube.client.YouTubeClient;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;


/**
 * Redis 조작 로직 중, 공용 코드를 제공하는 탬플릿 클래스
 * @author kcw
 * @since 2026-01-05
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class YouTubeCodeTemplate {

    public static List<YouTubeApiDto.Detail> searchAndGetDetails(
            YouTubeClient youTubeClient,
            String query
    ) {

        // [1] 검색 수행 후, 영상 번호만 추출
        List<String> videoIds = youTubeClient.searchVideo(query)
                .stream()
                .map(YouTubeApiDto.Row::getVideoId)
                .filter(Objects::nonNull)
                .toList();

        // [2] 영상 상세조회 후 결과 반환
        return youTubeClient.getVideos(videoIds);
    }

}