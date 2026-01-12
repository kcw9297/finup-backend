package app.finup.layer.domain.video.service;

import app.finup.infra.api.youtube.dto.YouTube;
import app.finup.infra.api.youtube.provider.YouTubeProvider;
import app.finup.infra.api.youtube.utils.YouTubeUtils;
import app.finup.layer.domain.video.dto.VideoDto;
import app.finup.layer.domain.video.dto.VideoDtoMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * VideoService 구현 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class VideoServiceImpl implements VideoService {

    private final YouTubeProvider youTubeProvider;

    @Override
    public VideoDto.Detail getDetail(String videoUrl) {

        // [1] 영상 상세 조회
        YouTube.Detail video = youTubeProvider.getVideo(YouTubeUtils.parseVideoId(videoUrl));

        // [2] 외부에 제공할 DTO로 변환 및 빈환
        return VideoDtoMapper.toDetail(video);
    }

    @Override
    public List<VideoDto.Row> search(String keyword) {

        // [1] 영상 상세 조회
        List<YouTube.Row> video = youTubeProvider.searchVideo(keyword);

        // [2] 외부에 제공할 DTO로 변환 및 빈환
        return video.stream().map(VideoDtoMapper::toRow).toList();
    }

    @Override
    public List<VideoDto.Row> recommendForHome() {
        return List.of();
    }

    @Override
    public List<VideoDto.RecommendRow> recommendForStock(String stockId, String stockName) {
        return List.of();
    }
}
