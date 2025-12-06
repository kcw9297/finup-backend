package app.finup.layer.domain.videolink.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.infra.youtube.dto.YouTube;
import app.finup.infra.youtube.provider.YouTubeProvider;
import app.finup.infra.youtube.utils.YouTubeUtils;
import app.finup.layer.base.utils.ReorderUtils;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.dto.VideoLinkDtoMapper;
import app.finup.layer.domain.videolink.entity.VideoLink;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;
import app.finup.layer.domain.videolink.repository.VideoLinkRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Objects;
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
    private final YouTubeProvider youTubeProvider;

    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> getListByStudyId(Long studyId) {

        return videoLinkRepository
                .findByVideoLinkOwnerAndOwnerId(VideoLinkOwner.STUDY, studyId)
                .stream()
                .map(this::getYouTubeInfoAndMapToRow)
                .toList();
    }

    // 유튜브 정보 조회 및 DTO 변환
    private VideoLinkDto.Row getYouTubeInfoAndMapToRow(VideoLink videoLink) {

        // [1] 유튜브 영상 정보 조회
        YouTube.Detail video = youTubeProvider.getVideo(videoLink.getVideoId());

        // [2] DTO 변환 및 반환
        return VideoLinkDtoMapper.toRow(videoLink, video);
    }


    @Override
    public void add(VideoLinkDto.Add rq) {

        // [1] 중복 링크 검증 (똑같은 영상 아이디로 조회한 경우)
        String videoId = YouTubeUtils.parseVideoId(rq.getVideoUrl());

        if (videoLinkRepository.existsByVideoId(videoId))
            throw new BusinessException(AppStatus.VIDEO_LINK_ALREADY_EXISTS);

        // [2] 정렬 순서 계산
        Double displayOrder = calculateNextOrder(rq.getLastVideoLinkId());

        // [3] YouTube 영상 메타데이터 조회
        YouTube.Detail video = youTubeProvider.getVideo(videoId);

        // [4] 엔티티 생성
        VideoLink videoLink = VideoLink.builder()
                .ownerId(rq.getOwnerId())
                .videoLinkOwner(rq.getVideoLinkOwner())
                .videoUrl(video.getVideoUrl())
                .videoId(video.getVideoId())
                .displayOrder(displayOrder)
                .build();

        // [5] 엔티티 저장
        videoLinkRepository.save(videoLink);
    }


    // 삽입할 단어의 정렬 값 계산
    private Double calculateNextOrder(Long lastVideoLinkId) {

        return Objects.isNull(lastVideoLinkId) ?
                ReorderUtils.DEFAULT_DISPLAY_ORDER :
                videoLinkRepository
                        .findById(lastVideoLinkId)
                        .map(ReorderUtils::calculateNextOrder)
                        .orElseThrow(() -> new BusinessException(AppStatus.STUDY_WORD_NOT_FOUND));
    }


    @Override
    public void edit(VideoLinkDto.Edit rq) {

        // [1] 비디오 링크 정보 조회
        VideoLink videoLink = videoLinkRepository
                .findById(rq.getVideoLinkId())
                .orElseThrow(() -> new BusinessException(AppStatus.VIDEO_LINK_NOT_FOUND));

        // [2] 정보 수정을 위한, 영상 정보 조회
        YouTube.Detail video = youTubeProvider.getVideo(rq.getVideoId());

        // [3] 영상 정보 기반 수정
        videoLink.edit(video.getVideoUrl(), video.getVideoId());
    }


    @Override
    public void reorder(VideoLinkDto.Reorder rq) {

        // [1] 정렬 대상 조회
        VideoLink targetLink =
                videoLinkRepository
                        .findById(rq.getVideoLinkId())
                        .orElseThrow(() -> new BusinessException(AppStatus.VIDEO_LINK_NOT_FOUND));

        // [2] 대상 양 옆의 영상 조회. 받아온 id가 null 인 경우, 조회하지 않음
        VideoLink prevLink = findLinkIfExists(rq.getPrevVideoLinkId());
        VideoLink nextLink = findLinkIfExists(rq.getNextVideoLinkId());

        // [3] displayOrder 계산 후 갱신
        targetLink.reorder(calculateNextOrder(rq, targetLink, prevLink, nextLink));
    }

    // 있는 영상 조회
    private VideoLink findLinkIfExists(Long videoLinkId) {

        return Objects.isNull(videoLinkId) ?
                null :
                videoLinkRepository
                        .findById(videoLinkId)
                        .orElseThrow(() -> new BusinessException(AppStatus.VIDEO_LINK_NOT_FOUND));
    }

    // 정렬 값 계산
    private Double calculateNextOrder(VideoLinkDto.Reorder rq,
                                      VideoLink targetLink, VideoLink prevLink, VideoLink nextLink) {  // 현재 학습에 필요한 전체 링크 조회

        // [1] 재정렬 수행
        Double displayOrder = ReorderUtils.calculateReorder(targetLink, prevLink, nextLink);

        // [2] 정렬 수행 후, 전체 재정렬이 필요한 경우 수행 (결과가 null이면 전체 재정렬 필요)
        return Objects.isNull(displayOrder) ? rebalanceAndReCalculate(rq, targetLink) : displayOrder;
    }

    // 전체 재정렬 (rebalance) 수행 후, 다시 계산
    private Double rebalanceAndReCalculate(VideoLinkDto.Reorder rq, VideoLink targetLink) {

        // [1] 현재 학습에 속한 전체 단어 조회
        List<VideoLink> videoLinks =
                videoLinkRepository.findByVideoLinkOwnerAndOwnerId(targetLink.getVideoLinkOwner(), targetLink.getOwnerId());

        // [2] 재정렬 수행
        ReorderUtils.rebalance(videoLinks); // 재정렬 수행

        // [3] 재정렬한 결과들을 Map으로 변환 (PK - Entity 자신 쌍)
        Map<Long, VideoLink> linkMap = videoLinks.stream()
                .collect(Collectors.toConcurrentMap(VideoLink::getVideoLinkId, Function.identity()));

        // [4] 재정렬 후 엔티티 조회 (이전, 이후 엔티티가 없다면 null)
        VideoLink newTargetLink = linkMap.get(rq.getVideoLinkId()); // 현재 링크 정보 (재정렬 후)
        VideoLink newPrevLink = Objects.isNull(rq.getPrevVideoLinkId()) ? null : linkMap.get(rq.getPrevVideoLinkId());
        VideoLink newNextLink = Objects.isNull(rq.getNextVideoLinkId()) ? null : linkMap.get(rq.getNextVideoLinkId());

        // [5] 다시 계산 후 반환
        return ReorderUtils.calculateReorder(newTargetLink, newPrevLink, newNextLink);
    }
}
