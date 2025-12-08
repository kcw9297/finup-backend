package app.finup.layer.domain.studyprogress.service;

import app.finup.layer.domain.studyprogress.dto.StudyProgressDto;

import java.util.List;

/**
 * 학습 진도 로직처리 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyProgressService {

    /**
     * 로그인 회원의 진도 목록 일괄 조회
     * @param memberId 회원번호
     * @return 회원 진도 목록
     */
    List<StudyProgressDto.Row> getMyList(Long memberId);


    /**
     * 학습 진도를 "학습 중" 상태로 변경
     * @param studyId 단계학습번호
     * @param memberId 회원번호
     */
    void start(Long studyId, Long memberId);


    /**
     * 학습 진도를 "학습 완료" 상태로 변경
     * @param studyId 단계학습번호
     * @param memberId 회원번호
     */
    void complete(Long studyId, Long memberId);
}
