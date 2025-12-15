package app.finup.layer.domain.videolink.service;

import app.finup.common.constant.Const;
import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.FormatUtils;
import app.finup.common.utils.LogUtils;
import app.finup.infra.youtube.dto.YouTube;
import app.finup.infra.youtube.provider.YouTubeProvider;
import app.finup.infra.youtube.utils.YouTubeUtils;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.dto.VideoLinkDtoMapper;
import app.finup.layer.domain.videolink.entity.VideoLink;
import app.finup.layer.domain.videolink.mapper.VideoLinkMapper;
import app.finup.layer.domain.videolink.repository.VideoLinkRepository;
import com.google.common.collect.Lists;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
        rows.forEach(row -> row.setDuration(FormatUtils.formatDuration(row.getDuration()))); // mybatis에서 문자열로 제공됨
        return Page.of(rows, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    public void add(VideoLinkDto.Add rq) {

        // [1] 중복 링크 검증 (똑같은 영상 아이디로 조회한 경우)
        String videoId = YouTubeUtils.parseVideoId(rq.getVideoUrl());

        if (videoLinkRepository.existsByVideoId((videoId)))
            throw new BusinessException(AppStatus.VIDEO_LINK_ALREADY_EXISTS, "videoUrl"); // 입력은 videoUrl로 받음

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
        List<VideoLink> videoLinks = videoLinkRepository
                .findByLastSyncedAtBefore(LocalDateTime.now(ZoneId.of(Const.ASIA_SEOUL)).minusHours(1));

        // [2] 연산이 간편하도록 map 변환
        Map<String, VideoLink> videoLinkMap = videoLinks.stream()
                .collect(Collectors.toConcurrentMap(
                        VideoLink::getVideoId,
                        Function.identity()
                ));

        // [3] YouTube API 호출 후 동기화 처리 (50개 씩)
        Lists.partition(new ArrayList<>(videoLinkMap.keySet()), 50)
                .forEach(chunk -> {
                    // 영상 조회
                    List<YouTube.Detail> videos = youTubeProvider.getVideos(chunk);

                    // 조회된 영상번호 목록 (숨김 처리되거나, 삭제된 영상은 결과에 미포함)
                    Set<String> resultIds = videos.stream()
                            .map(YouTube.Detail::getVideoId)
                            .collect(Collectors.toSet());

                    // 현재 chunk id set
                    Set<String> requestIds = new HashSet<>(chunk);

                    // 전체 요청 id에서 숨김, 삭제처리된 id만 조회
                    requestIds.removeAll(resultIds);

                    // 조회된 영상은 갱신 처리
                    videos.forEach(video -> {

                        // 갱신 대상
                        VideoLink videoLink = videoLinkMap.get(video.getVideoId());
                        log.warn("비디오 갱신 : video = {}", video);
                        try {

                            videoLink.sync(
                                    video.getTitle(), video.getThumbnailUrl(), video.getChannelTitle(),
                                    video.getDuration(), video.getViewCount(), video.getLikeCount(), video.getTags())
                            ;
                            // 영상 갱신에 실패해도, 다음 영상 동기화 수행
                        } catch (Exception e) {
                            LogUtils.showError(this.getClass(), "유튜브 영상 동기화 실패!\n영상 정보 : %s, YouTube API 조회 정보 : %s", videoLink, video);
                        }
                    });

                    // 조회되지 않은 숨김/삭제 영상은 삭제 처리
                    if (!requestIds.isEmpty()) videoLinkRepository.deleteByVideoIds(requestIds);
                });
    }


    @Override
    public VideoLinkDto.Row edit(VideoLinkDto.Edit rq) {

        // [1] 비디오 링크 정보 조회
        VideoLink videoLink = videoLinkRepository
                .findById(rq.getVideoLinkId())
                .orElseThrow(() -> new BusinessException(AppStatus.VIDEO_LINK_NOT_FOUND));

        // [2] 필요 정보 추출
        String videoUrl = rq.getVideoUrl();
        String videoId = YouTubeUtils.parseVideoId(videoUrl);

        // [3] 중복 검증
        if (Objects.equals(videoLink.getVideoId(), videoId) || videoLinkRepository.existsByVideoId((videoId)))
            throw new BusinessException(AppStatus.VIDEO_LINK_ALREADY_EXISTS, "videoUrl"); // 입력은 videoUrl로 받음

        // [4] 정보 수정을 위한, 영상 정보 조회
        YouTube.Detail video = youTubeProvider.getVideo(videoId);

        // [5] 영상 정보 기반 수정
        videoLink.edit(
                videoUrl, videoId, video.getTitle(), video.getThumbnailUrl(), video.getChannelTitle(),
                video.getDuration(), video.getViewCount(), video.getLikeCount(), video.getTags()
        );

        // [6] 갱신된 영상 정보 반환
        return VideoLinkDtoMapper.toRow(videoLink);
    }


    @Override
    public void remove(Long videoLinkId) {
        videoLinkRepository.deleteById(videoLinkId);
    }

}
