package app.finup.layer.base.template;

import app.finup.infra.api.youtube.dto.YouTube;
import app.finup.infra.api.youtube.provider.YouTubeProvider;
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

    public static List<YouTube.Detail> searchAndGetDetails(
            YouTubeProvider youTubeProvider,
            String query
    ) {

        // [1] 검색 수행 후, 영상 번호만 추출
        List<String> videoIds = youTubeProvider.searchVideo(query)
                .stream()
                .map(YouTube.Row::getVideoId)
                .filter(Objects::nonNull)
                .toList();

        // [2] 영상 상세조회 후 결과 반환
        return youTubeProvider.getVideos(videoIds);
    }

}