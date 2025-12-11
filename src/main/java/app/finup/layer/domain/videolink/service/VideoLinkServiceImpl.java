package app.finup.layer.domain.videolink.service;

import app.finup.common.constant.Const;
import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.infra.youtube.dto.YouTube;
import app.finup.infra.youtube.provider.YouTubeProvider;
import app.finup.infra.youtube.utils.YouTubeUtils;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.entity.VideoLink;
import app.finup.layer.domain.videolink.mapper.VideoLinkMapper;
import app.finup.layer.domain.videolink.repository.VideoLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.List;

/**
 * VideoLinkService 구현 클래스
 * @author kcw
 * @since 2025-12-04
 */

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VideoLinkServiceImpl implements VideoLinkService {

    private final VideoLinkRepository videoLinkRepository;
    private final VideoLinkMapper videoLinkMapper;
    private final YouTubeProvider youTubeProvider;

    @Override
    @Transactional(readOnly = true)
    public Page<VideoLinkDto.Row> getPagedList(VideoLinkDto.Search rq) {

        // [1] 페이징 검색
        List<VideoLinkDto.Row> rows = videoLinkMapper.search(rq);
        Integer count = videoLinkMapper.countForSearch(rq);

        // [2] 페이징 객체 매핑 및 반환
        return Page.of(rows, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    public void add(VideoLinkDto.Add rq) {

        // [1] 중복 링크 검증 (똑같은 영상 아이디로 조회한 경우)
        String videoId = YouTubeUtils.parseVideoId(rq.getVideoUrl());

        if (videoLinkRepository.existsByVideoId((videoId)))
            throw new BusinessException(AppStatus.VIDEO_LINK_ALREADY_EXISTS);

        // [2] YouTube 영상 메타데이터 조회 (단일 정보 조회는 캐시처리)
        YouTube.Detail video = youTubeProvider.getVideo(videoId);

        // [3] 엔티티 생성
        VideoLink videoLink = VideoLink.builder()
                .videoUrl(video.getVideoUrl())
                .videoId(video.getVideoId())
                .title(video.getTitle())
                .thumbnailUrl(video.getThumbnailUrl())
                .channelTitle(video.getChannelTitle())
                .duration(video.getDuration())
                .publishedAt(LocalDateTime.ofInstant(video.getPublishedAt(), ZoneId.of(Const.ASIA_SEOUL)))
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .tags(video.getTags())
                .build();

        // [4] 엔티티 저장
        videoLinkRepository.save(videoLink);
    }


    @Override
    public void sync() {

        // [1] 동기화가 필요한 모든 영상 조회
        // 동기화일 기준 일정 시간이 흐른 데이터 일괄 조회 (현재는 1시간 간격으로 함)
        //List<VideoLink> videoLinks = videoLinkRepository
        //        .findByLastSyncedAtBefore(LocalDateTime.now(ZoneId.of(Const.ASIA_SEOUL)).plusHours(1));

    }


    @Override
    public void edit(VideoLinkDto.Edit rq) {

        // [1] 비디오 링크 정보 조회
        VideoLink videoLink = videoLinkRepository
                .findById(rq.getVideoLinkId())
                .orElseThrow(() -> new BusinessException(AppStatus.VIDEO_LINK_NOT_FOUND));

        // [2] 정보 수정을 위한, 영상 정보 조회
        YouTube.Detail video = youTubeProvider.getVideo(videoLink.getVideoId());

        // [3] 영상 정보 기반 수정
        videoLink.edit(
                rq.getVideoUrl(), video.getVideoId(), video.getTitle(), video.getThumbnailUrl(), video.getChannelTitle(),
                video.getDuration(), video.getViewCount(), video.getLikeCount(), video.getTags()
        );
    }


    @Override
    public void remove(Long videoLinkId) {
        videoLinkRepository.deleteById(videoLinkId);
    }

}
