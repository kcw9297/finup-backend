package app.finup.layer.domain.level.service;


import app.finup.layer.domain.level.dto.LevelDto;

import java.util.List;

/**
 * 개념 학습 단계 Service 인터페이스
 * - 단계 조회/등록/수정/삭제
 * - 회원별 진행도 관리
 * @author sjs
 * @since 2025-12-05
 */
public interface LevelService {

    /**
     * 단계 리스트 조회
     * @param memberId 로그인 회원 (비로그인 시 null)
     */
    List<LevelDto.Row> getList(Long memberId);

    /**
     * 단계 상세 조회
     */
    LevelDto.Detail getDetail(Long levelId);
    /**
     * 관리자: 단계 등록
     */
    Long write(LevelDto.Write dto);

    /**
     * 관리자: 단계 수정
     */
    void edit(LevelDto.Edit dto);

    /**
     * 관리자: 단계 삭제
     */
    void delete(Long levelId);

    /**
     * 회원: 단계 진도 저장/업데이트
     */
    void updateProgress(Long memberId, Long levelId, Integer progress);
}
