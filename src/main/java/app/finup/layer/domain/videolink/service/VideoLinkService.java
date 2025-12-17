package app.finup.layer.domain.videolink.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.videolink.dto.VideoLinkDto;


/**
 * 학습 영상 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface VideoLinkService {

    /**
     * 특정 자원에 속하는 학습 영상 목록 조회
     * @param rq 영상 검색요청 DTO
     * @return 페이징된 영상 목록 DTO
     */
    Page<VideoLinkDto.Row> getPagedList(VideoLinkDto.Search rq);


    /**
     * 학습영상 링크 추가
     * @param rq 영상 추가요청 DTO
     */
    void add(VideoLinkDto.Add rq);


    /**
     * 학습영상 정보 동기화 (마지막 동기화 시점 기준으로 판단)
     */
    void sync();


    /**
     * 학습영상 링크 정보 변경 (정렬 순서를 바꾸지 않음)
     * @param rq 영상 추가요청 DTO
     * @return 갱신된 결과 DTO (목록에서 직접 클릭하여 모달로 수정하므로, Row 반환)
     */
    VideoLinkDto.Row edit(VideoLinkDto.Edit rq);


    /**
     * 학습영상 삭제
     * @param videoLinkId 학습영상 링크 번호
     */
    void remove(Long videoLinkId);
}
