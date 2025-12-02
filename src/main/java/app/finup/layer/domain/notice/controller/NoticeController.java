package app.finup.layer.domain.notice.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 관리자 공지사항 컨트롤러 클래스
 * @author khj
 * @since 2025-12-01
 */

@Slf4j
@RestController
@RequestMapping(Url.NOTICE_ADMIN_API)
@RequiredArgsConstructor
public class NoticeController {
    private final NoticeService noticeService;

    /**
     * 게시글 조회 API
     * [GET] /admin/notices
     * @param rq 게시글 조회 요청 DTO
     */
/*
    @GetMapping
    public ResponseEntity<?> getList(NoticeDto.Search rq) {

        // [1] 요청
        Page<NoticeDto.NoticeList> rp = noticeService.search(rq);

        // [2] 페이징 응답 전달
        return Api.ok(rp.getList(), Pagination.of(rp));
    }
*/
    /**
     * 게시글 검색 API
     * [GET] /admin/notices/search
     * @param rq 게시글 검색 요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(NoticeDto.Search rq) {

        // [1] 요청
        Page<NoticeDto.Summary> rp = noticeService.search(rq);

        // [2] 페이징 응답 전달
        return Api.ok(rp.getList(), Pagination.of(rp));
    }

    /**
     * 공지사항 상세 조회 API
     * [GET] /admin/notices/detail?noticeId={id}
     */
    @GetMapping("/detail")
    public ResponseEntity<?> detail(@RequestParam Long noticeId) {
        // [1] 상세 조회 요청
        NoticeDto.Detail detail = noticeService.getDetail(noticeId);

        // [2] 데이터 반환
        return Api.ok(detail);
    }
}
