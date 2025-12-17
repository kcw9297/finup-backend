package app.finup.layer.domain.studyword.service;

import app.finup.layer.domain.studyword.dto.StudyWordDto;

import java.util.List;

/**
 * 학습 단어 추천 로직을 취급하는 인터페이스 (메인 비즈니스 로직과 분리)
 * @author kcw
 * @since 2025-12-17
 */
public interface StudyWordRecommendService {

    /**
     * 학습 단어 추천 (학습 페이지)
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<StudyWordDto.Row> recommendForStudy(Long memberId, Long studyId);


    /**
     * 학습 단어 재추천 (학습 페이지)
     * @param studyId  대상 학습번호
     * @param memberId 추천 대상 회원번호
     * @return 게시할 추천 영상 DTO 리스트
     */
    List<StudyWordDto.Row> retryRecommendForStudy(Long studyId, Long memberId);

}
