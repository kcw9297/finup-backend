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
    public List<VideoLinkDto.Row> getList(Long ownerId, VideoLinkOwner videoLinkOwner) {

        return videoLinkRepository
                .findByOwnerIdAndVideoLinkOwner(ownerId, videoLinkOwner)
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
        Double displayOrder = calculateNextOrder(rq.getOwnerId(), rq.getVideoLinkOwner());

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
    private Double calculateNextOrder(Long ownerId, VideoLinkOwner videoLinkOwner) {

        return Objects.isNull(ownerId) ?

                // 소유자번호가 없는 경우 (현재는 HOME 요청인 경우)
                videoLinkRepository
                        .findLastByVideoLinkOwner(videoLinkOwner)
                        .map(ReorderUtils::calculateNextOrder)
                        .orElse(ReorderUtils.DEFAULT_DISPLAY_ORDER) :

                // 소유자 번호가 있는 경우
                videoLinkRepository
                        .findLastByOwnerIdAndVideoLinkOwner(ownerId, videoLinkOwner)
                        .map(ReorderUtils::calculateNextOrder)
                        .orElse(ReorderUtils.DEFAULT_DISPLAY_ORDER);
    }


    @Override
    public void reorder(VideoLinkDto.Reorder rq) {

        // [1] 정렬 대상 및 전체 목록 조회
        VideoLink targetLink =
                videoLinkRepository
                        .findById(rq.getVideoLinkId())
                        .orElseThrow(() -> new BusinessException(AppStatus.VIDEO_LINK_NOT_FOUND));

        // displayOrder 순 정렬된 목록
        List<VideoLink> videoLinks =
                videoLinkRepository.findByOwnerIdAndVideoLinkOwner(rq.getOwnerId(), rq.getVideoLinkOwner());


        // [2] displayOrder 계산 후 갱신
        targetLink.reorder(calculateOrder(videoLinks, rq.getReorderPosition()));
    }

    // 정렬 시도 후, 재정렬이 필요하면 재정렬 수행 후 다시 정렬
    private Double calculateOrder(List<VideoLink> videoLinks, Integer reorderPosition) {

        // [1] 재정렬 수행
        Double displayOrder = ReorderUtils.calculateReorder(videoLinks, reorderPosition);// 재정렬 수행

        // [2] 만약 null 반환 시, 일괄 재정렬 후 재시도
        if (Objects.isNull(displayOrder))
            displayOrder = ReorderUtils.rebalanceAndReorder(videoLinks, reorderPosition);

        // [3] 계산된 재정렬 값 반환
        return displayOrder;
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
    public void remove(Long videoLinkId) {
        videoLinkRepository.deleteById(videoLinkId);
    }

}
