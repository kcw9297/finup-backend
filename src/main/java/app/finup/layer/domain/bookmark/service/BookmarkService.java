package app.finup.layer.domain.bookmark.service;

import app.finup.layer.domain.bookmark.dto.BookmarkDto;
import app.finup.layer.domain.bookmark.enums.BookmarkTarget;

import java.util.List;

/**
 * 북마크 로직처리 서비스 인터페이스
 * @author kcw
 * @since 2025-12-02
 */
public interface BookmarkService {

    /**
     * 로그인 회원의 북마크 정보 일괄 조회
     * @param memberId 로그인 회원번호
     * @return 회원의 북마크 정보 DTO 리스트
     */
    List<BookmarkDto.Row> getMyList(Long memberId);


    /**
     * 북마크 추가
     * @param rq 북마크 요청 DTO
     */
    void add(BookmarkDto.Add rq);


    /**
     * 북마크 해제
     * @param targetId 대상 고유번호
     * @param bookmarkTarget 대상 정보 (NEWS, STUDY, ...)
     */
    void remove(Long targetId, BookmarkTarget bookmarkTarget);
}
