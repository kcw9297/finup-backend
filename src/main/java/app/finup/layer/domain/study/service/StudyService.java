package app.finup.layer.domain.study.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.study.dto.StudyDto;


/**
 * 단계별 학습 핵심로직 처리 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface StudyService {

    /**
     * 페이징 리스트 조회 (무한 스크롤)
     * @param rq 페이징 요청 DTO
     * @return 페이징된 단계 학습 목록 DTO
     */
    Page<StudyDto.Row> getPagedList(StudyDto.Search rq);


    /**
     * 특정 단계학습 정보 조회
     * @param studyId 대상 단계학습번호
     * @return 조회된 단일 학습정보 DTO
     */
    StudyDto.Detail getDetail(Long studyId);


    /**
     * 단계학습 자료 추가
     * @param rq 추가 요청 DTO
     */
    void add(StudyDto.Add rq);


    /**
     * 단계학습 자료 수정
     * @param rq 수정 요청 DTO
     */
    void edit(StudyDto.Edit rq);


    /**
     * 단계학습 자료 삭제
     * @param studyId 대상 단계학습번호
     */
    void remove(Long studyId);

}
