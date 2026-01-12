package app.finup.layer.base.template;

import com.example.demo.infra.api.youtube.dto.YouTube;
import com.example.demo.infra.api.youtube.provider.YouTubeProvider;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Objects;



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