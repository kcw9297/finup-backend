package app.finup.layer.domain.video.service;

import app.finup.api.external.youtube.dto.YouTubeApiDto;
import app.finup.api.external.youtube.client.YouTubeClient;
import app.finup.api.external.youtube.utils.YouTubeUtils;
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

    private final YouTubeClient youTubeClient;

    @Override
    public VideoDto.Detail getDetail(String videoUrl) {

        // [1] 영상 상세 조회
        YouTubeApiDto.Detail video = youTubeClient.getVideo(YouTubeUtils.parseVideoId(videoUrl));

        // [2] 외부에 제공할 DTO로 변환 및 빈환
        return VideoDtoMapper.toDetail(video);
    }

    @Override
    public List<VideoDto.Row> search(String keyword) {

        // [1] 영상 상세 조회
        List<YouTubeApiDto.Row> video = youTubeClient.searchVideo(keyword);

        // [2] 외부에 제공할 DTO로 변환 및 빈환
        return video.stream().map(VideoDtoMapper::toRow).toList();
    }

}
