package app.finup.layer.domain.notice.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.notice.dto.NoticeDto;
import org.springframework.data.domain.Pageable;

/**
 * 공지사항 게시판 비즈니스 로직 인터페이스
 * @author khj
 * @since 2025-12-01
 */

public interface NoticeService {
    /**
     * 게시글 작성
     * @param rq 작성 요청 DTO
     * @return 생성된 게시글 번호
     */
    Long write(NoticeDto.Write rq);

    /**
     * 게시글 수정
     * @param rq 수정 요청 DTO
     */
    void edit(NoticeDto.Edit rq);

    /**
     * 게시글 삭제
     * @param noticeId 삭제 요청 게시글 아이디(번호)
     */
    void remove(Long noticeId);

    /**
     * 게시글 조회
     * @param noticeId 수정 요청
     * @return 생성된 게시글 번호
     */
    NoticeDto.Detail getDetail(Long noticeId);


    /**
     * 게시글 목록 일괄 조회
     * @return 조회된 게시글 DTO 리스트
     */
    // Page<NoticeDto.NoticeList> getList();

    Page<NoticeDto.NoticeList> getList(NoticeDto.);
}
