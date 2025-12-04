package app.finup.layer.domain.videolink.service;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
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
import java.util.Objects;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class VideoLinkServiceImpl implements VideoLinkService {

    private final VideoLinkRepository videoLinkRepository;

    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> getListByStudyId(Long studyId) {

        return videoLinkRepository
                .findByVideoLinkOwnerAndOwnerId(VideoLinkOwner.STUDY, studyId)
                .stream()
                .map(VideoLinkDtoMapper::toRow)
                .toList();
    }


    @Override
    @Transactional(readOnly = true)
    public List<VideoLinkDto.Row> getListForHome() {

        return videoLinkRepository
                .findByVideoLinkOwner(VideoLinkOwner.HOME)
                .stream()
                .map(VideoLinkDtoMapper::toRow)
                .toList();
    }


    @Override
    public void add(VideoLinkDto.Add rq) {

        // [1] 정렬 순서 계산
        Double displayOrder = calculateNextOrder(rq.getLastVideoLinkId());

        // TODO [2] 엔티티 생성 (여기는 유튜브 API를 이용해야 함 임시)
        VideoLink videoLink = VideoLink.builder()
                .ownerId(rq.getOwnerId())
                .videoLinkOwner(rq.getVideoLinkOwner())
                .videoUrl(rq.getVideoUrl())
                .videoId(rq.getVideoId())
                .thumbnailUrl(rq.getThumbnailUrl())
                .title(rq.getTitle())
                .displayOrder(displayOrder)
                .build();

        // [3] 엔티티 저장
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

    }


    @Override
    public void reorder(VideoLinkDto.Reorder rq) {

    }
}
