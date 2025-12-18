package app.finup.layer.domain.videolink.service;

import app.finup.layer.domain.videolink.dto.VideoLinkDto;

import java.util.List;

/**
 * 학습 영상 추천 로직을 취급하는 인터페이스 (메인 비즈니스 로직과 분리)
 * @author kcw
 * @since 2025-12-17
 */
public interface VideoLinkRecommendService {


    /**
     * 학습 영상 추천 (페이지 로그아웃 홈)
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<VideoLinkDto.Row> recommendForLogoutHome();


    /**
     * 학습 영상 추천 (페이지 홈)
     * @param memberId 추천 대상 회원번호
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<VideoLinkDto.Row> recommendForLoginHome(Long memberId);


    /**
     * 학습 영상 재추천 (페이지 홈)
     * @param memberId 추천 대상 회원번호
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<VideoLinkDto.Row> retryRecommendForLoginHome(Long memberId);


    /**
     * 학습 영상 추천 (학습 페이지)
     * @param studyId  대상 학습번호
     * @param memberId 추천 대상 회원번호
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<VideoLinkDto.Row> recommendForStudy(Long studyId, Long memberId);


    /**
     * 학습 영상 재추천 (학습 페이지)
     * @param studyId  대상 학습번호
     * @param memberId 추천 대상 회원번호
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<VideoLinkDto.Row> retryRecommendForStudy(Long studyId, Long memberId);


    /**
     * 홈 화면 기본 영상 목록 조회 (관리자 등록 기준)
     * @param size 조회 개수
     * @return 영상 목록
     */
    List<VideoLinkDto.Row> getHomeLatestList(Integer size);

}
