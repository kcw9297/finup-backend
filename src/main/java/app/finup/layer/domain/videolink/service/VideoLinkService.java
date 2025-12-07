package app.finup.layer.domain.videolink.service;

import app.finup.layer.domain.videolink.dto.VideoLinkDto;
import app.finup.layer.domain.videolink.enums.VideoLinkOwner;

import java.util.List;

/**
 * 학습 영상 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface VideoLinkService {

    /**
     * 특정 자원에 속하는 학습 영상 목록 조회
     * @param ownerId 소유자 고유번호(PK)
     * @param videoLinkOwner 소유자 정보 (HOME, STUDY, ...)
     */
    List<VideoLinkDto.Row> getList(Long ownerId, VideoLinkOwner videoLinkOwner);


    /**
     * 학습영상 링크 추가
     * @param rq 영상 추가요청 DTO
     */
    void add(VideoLinkDto.Add rq);


    /**
     * 학습영상 순서 재정렬(변경)
     * @param rq 영상 재정렬 요청 DTO
     */
    void reorder(VideoLinkDto.Reorder rq);


    /**
     * 학습영상 링크 정보 변경 (정렬 순서를 바꾸지 않음)
     * @param rq 영상 추가요청 DTO
     */
    void edit(VideoLinkDto.Edit rq);


    /**
     * 학습영상 삭재
     * @param videoLinkId 학습영상 링크 번호
     */
    void remove(Long videoLinkId);
}
