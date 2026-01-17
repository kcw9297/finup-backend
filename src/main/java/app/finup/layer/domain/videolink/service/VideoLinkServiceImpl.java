package app.finup.layer.domain.videolink.service;

import app.finup.common.constant.Const;
import app.finup.common.dto.Page;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.TimeUtils;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.infra.ai.EmbeddingProvider;
import app.finup.common.utils.AiUtils;
import app.finup.api.external.youtube.dto.YouTubeApiDto;
import app.finup.api.external.youtube.client.YouTubeClient;
import app.finup.api.external.youtube.utils.YouTubeUtils;
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

    // 사용 의존성
    private final VideoLinkRepository videoLinkRepository;
    private final VideoLinkMapper videoLinkMapper;
    private final YouTubeClient youTubeClient;
    private final EmbeddingProvider embeddingProvider;

    // 사용 상수
    private static final int SPLIT_DESCRIPTION_LEN = 200;


    @Override
    @Transactional(readOnly = true)
    public Page<VideoLinkDto.Row> getPagedList(VideoLinkDto.Search rq) {

        // [1] 페이징 검색
        List<VideoLinkDto.Row> rows = videoLinkMapper.search(rq);
        Integer count = videoLinkMapper.countForSearch(rq);

        // [2] 페이징 객체 매핑 및 반환
        rows.forEach(row -> row.setDuration(TimeUtils.formatDuration(row.getDuration()))); // mybatis에서 문자열로 제공됨
        return Page.of(rows, count, rq.getPageNum(), rq.getPageSize());
    }


    @Override
    public void add(VideoLinkDto.Add rq) {

        // [1] 중복 링크 검증 (똑같은 영상 아이디로 조회한 경우)
        String videoId = YouTubeUtils.parseVideoId(rq.getVideoUrl());

        if (videoLinkRepository.existsByVideoId((videoId)))
            throw new BusinessException(AppStatus.VIDEO_LINK_ALREADY_EXISTS, "videoUrl"); // 입력은 videoUrl로 받음

        // [2] YouTube 영상 메타데이터 조회 (단일 정보 조회는 캐시처리)
        YouTubeApiDto.Detail video = youTubeClient.getVideo(videoId);

        // [3] 임베딩 텍스트 생성
        String description = StrUtils.splitWithStart(video.getDescription(), SPLIT_DESCRIPTION_LEN); // 200자까지만 사용 (null safe)
        String text = AiUtils.generateEmbeddingText(video.getTitle(), video.getChannelTitle(), description, video.getTags());
        byte[] embedding = embeddingProvider.generate(text);

        // [4] 엔티티 생성
        VideoLink videoLink = VideoLink.builder()
                .videoUrl(video.getVideoUrl())
                .videoId(video.getVideoId())
                .title(video.getTitle())
                .thumbnailUrl(video.getThumbnailUrl())
                .channelTitle(video.getChannelTitle())
                .description(description)
                .duration(video.getDuration())
                .publishedAt(LocalDateTime.ofInstant(video.getPublishedAt(), ZoneId.of(Const.ASIA_SEOUL)))
                .viewCount(video.getViewCount())
                .likeCount(video.getLikeCount())
                .tags(video.getTags())
                .embedding(embedding)
                .build();

        // [5] 엔티티 저장
        videoLinkRepository.save(videoLink);
    }


    @Override
    public void sync() {

        // [1] 동기화가 필요한 모든 영상 조회
        // 동기화일 기준 일정 시간이 흐른 데이터 일괄 조회 (현재는 1시간 간격으로 함)
        List<VideoLink> videoLinks = videoLinkRepository
                .findByLastSyncedAtBefore(LocalDateTime.now(ZoneId.of(Const.ASIA_SEOUL)).minusMinutes(5));

        // [2] 연산이 간편하도록 map 변환
        Map<String, VideoLink> videoLinkMap = videoLinks.stream()
                .collect(Collectors.toConcurrentMap(
                        VideoLink::getVideoId,
                        Function.identity()
                ));

        // [3] 동기화 처리 (50개 씩)
        Lists.partition(new ArrayList<>(videoLinkMap.keySet()), 50)
                .forEach(chunk -> {
                    // 영상 조회
                    List<YouTubeApiDto.Detail> videos = youTubeClient.getVideos(chunk);

                    // 조회된 영상번호 목록 (숨김 처리되거나, 삭제된 영상은 결과에 미포함)
                    Map<String, YouTubeApiDto.Detail> resultVideoMap = videos.stream()
                            .collect(Collectors.toConcurrentMap(
                                    YouTubeApiDto.Detail::getVideoId,
                                    Function.identity()
                            ));

                    // 현재 chunk id set
                    Set<String> requestIds = new HashSet<>(chunk);

                    // 전체 요청 id에서 숨김, 삭제처리된 id만 조회
                    requestIds.removeAll(resultVideoMap.keySet());

                    // 조회된 영상은 갱신 처리
                    syncVideos(videoLinkMap, resultVideoMap);

                    // 조회되지 않은 숨김/삭제 영상은 삭제 처리
                    if (!requestIds.isEmpty()) videoLinkRepository.deleteByVideoIds(requestIds);
                });
    }

    // 조회된 비디오 정보 갱신
    private void syncVideos(Map<String, VideoLink> videoLinkMap, Map<String, YouTubeApiDto.Detail> resultVideoMap) {

        // [1] embedding 변동이 필요한 경우 추출
        Map<String, String> need = resultVideoMap.entrySet()
                .stream()
                .filter(entry -> needEmbed(videoLinkMap.get(entry.getKey()), entry.getValue()))
                .collect(Collectors.toConcurrentMap(
                        Map.Entry::getKey,
                        entry -> {
                            YouTubeApiDto.Detail video = entry.getValue();
                            String description = StrUtils.splitWithStart(video.getDescription(), SPLIT_DESCRIPTION_LEN); // 200자까지만 사용 (null safe)
                            return AiUtils.generateEmbeddingText(video.getTitle(), video.getChannelTitle(), description, video.getTags());
                        }
                ));

        // [2] 임베딩 수행
        Map<String, byte[]> embeddingMap = embeddingProvider.generate(need);

        // [3] 갱신 수행
        resultVideoMap.forEach((videoId, video) -> {

            try {

                // 갱신 대상 정보 추출
                VideoLink videoLink = videoLinkMap.get(videoId);
                byte[] embedding = embeddingMap.get(videoId);

                // embedVideo 존재 시, 임베딩 정보 갱신 필요
                String description = StrUtils.splitWithStart(video.getDescription(), SPLIT_DESCRIPTION_LEN);

                // 임베딩 문자가 존재하는 경우 포함하여 갱신
                if (Objects.isNull(embedding))
                    videoLink.sync(
                            video.getTitle(), video.getThumbnailUrl(), video.getChannelTitle(), description,
                            video.getDuration(), video.getViewCount(), video.getLikeCount(), video.getTags());

                else
                    videoLink.sync(
                            video.getTitle(), video.getThumbnailUrl(), video.getChannelTitle(), description,
                            video.getDuration(), video.getViewCount(), video.getLikeCount(), video.getTags(), embedding);


                // 영상 갱신에 실패해도, 다음 영상 동기화 수행
            } catch (Exception e) {
                LogUtils.showError(this.getClass(), "유튜브 영상 동기화 실패!\n영상 정보 : %s, YouTube API 조회 정보 : %s",
                        videoLinkMap.get(videoId), video
                );
            }
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
        YouTubeApiDto.Detail video = youTubeClient.getVideo(videoId);

        // [5] 임베딩 텍스트 생성
        String description = StrUtils.splitWithStart(video.getDescription(), SPLIT_DESCRIPTION_LEN); // 200자까지만 사용
        String text = AiUtils.generateEmbeddingText(video.getTitle(), video.getChannelTitle(), description, video.getTags());
        byte[] embedding = embeddingProvider.generate(text);


        // [6] 영상 정보 기반 수정
        videoLink.edit(
                videoUrl, videoId, video.getTitle(), video.getThumbnailUrl(), video.getChannelTitle(), description,
                video.getDuration(), video.getViewCount(), video.getLikeCount(), video.getTags(), embedding
        );

        // [7] 갱신된 영상 정보 반환
        return VideoLinkDtoMapper.toRow(videoLink);
    }


    @Override
    public void remove(Long videoLinkId) {
        videoLinkRepository.deleteById(videoLinkId);
    }


    // 주어진 필드가 일치하는지 검증 (임베딩 필드 갱신 목적)
    private boolean needEmbed(VideoLink videoLink, YouTubeApiDto.Detail video) {

        return !Objects.equals(videoLink.getTitle(), video.getTitle()) ||
                !Objects.equals(videoLink.getChannelTitle(), video.getChannelTitle()) ||
                !Objects.equals(videoLink.getDescription(), video.getDescription()) ||
                !Objects.equals(videoLink.getTags(), video.getTags());
    }
}
