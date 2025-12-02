package app.finup.layer.domain.videolink.service;

import app.finup.layer.domain.videolink.dto.VideoLinkDto;

import java.util.List;

/**
 * 학습용 비디오 링크 로직처리 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface VideoLinkService {


    /**
     * 특정 단계 학습에 속하는 비디오 링크 목록 조회
     * @param studyId 단계 학습번호
     */
    List<VideoLinkDto.Summary> getListByStudy(Long studyId);


    /**
     * 페이지 홈에 속하는 비디오 링크 목록 조회
     */
    List<VideoLinkDto.Summary> getListByHome();


    /**
     * 학습영상 링크 추가
     * @param rq 영상 추가요청 DTO
     */
    void add(VideoLinkDto.Add rq);


    /**
     * 학습영상 링크 정보 변경 (정렬 순서를 바꾸지 않음)
     * @param rq 영상 추가요청 DTO
     */
    void edit(VideoLinkDto.Edit rq);


    /**
     * 학습영상 순서 재정렬(변경)
     * @param rq 영상 재정렬 요청 DTO
     */
    void reorder(VideoLinkDto.Reorder rq);
}
