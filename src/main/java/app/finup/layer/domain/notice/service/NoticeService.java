package app.finup.layer.domain.notice.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.notice.dto.NoticeDto;

import java.util.List;

/**
 * 공지사항 게시판 비즈니스 로직 인터페이스
 * @author khj
 * @since 2025-12-01
 */

public interface NoticeService {

    /**
     * 게시글 조회
     * @param noticeId 조회 대상 공지사항번호
     * @return 조회된 게시글 상세 정보 DTO
     */
    NoticeDto.Detail getDetail(Long noticeId);


    /**
     * 게시글 검색
     * @param rq 페이징 요청 DTO
     * @return 검색된 게시글 DTO 페이지 리스트
     */
    Page<NoticeDto.Row> getPagedList(NoticeDto.Search rq);


    /**
     * 페이지 홈에 게시할 공지사항 목록
     * @return 페이지 홈에 게시할 공지사항 DTO 리스트
     */
    List<NoticeDto.Row> getHomeList();


    /**
     * 게시글 작성
     * @param rq 작성 요청 DTO
     */
    void write(NoticeDto.Write rq);


    /**
     * 게시글 수정
     * @param rq 수정 요청 DTO
     */
    void edit(NoticeDto.Edit rq);


    /**
     * 게시글 조회수 동기화
     */
    void syncViewCount();


    /**
     * 게시글 삭제
     * @param noticeId 삭제 요청 게시글 아이디(번호)
     */
    void remove(Long noticeId);

}
