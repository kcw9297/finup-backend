package app.finup.layer.domain.reboard.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.reboard.dto.ReboardDto;

import java.util.List;

/**
 * 게시판 비즈니스 로직 인터페이스
 * @author kcw
 * @since 2025-11-24
 */

public interface ReboardService {

    /**
     * 게시글 작성
     * @param rq 작성 요청 DTO
     * @return 생성된 게시글 번호
     */
    Long write(ReboardDto.Write rq);

    /**
     * 게시글 수정
     * @param rq 수정 요청 DTO
     */
    void edit(ReboardDto.Edit rq);

    /**
     * 게시글 삭제
     * @param idx 대상 게시글 번호
     */
    void remove(Long idx);

    /**
     * 게시글 검색
     * @param rq 검색 요청 DTO
     * @return 페이징된 DTO 리스트
     */
    Page<ReboardDto.Row> search(ReboardDto.Search rq);

    /**
     * 게시글 상세 조회
     * @param idx 대상 게시글 번호
     * @return 조회된 게시글 상세 정보 DTO
     */
    ReboardDto.Detail getDetail(Long idx);

    /**
     * 게시글 목록 일괄 조회
     * @return 조회된 게시글 DTO 리스트
     */
    List<ReboardDto.Row> getList();
}
