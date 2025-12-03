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
import org.springframework.web.bind.annotation.*;

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
     * 게시글 검색 API
     * [GET] /admin/api/notices/search
     * @param rq 게시글 검색 요청 DTO
     */
    @GetMapping
    public ResponseEntity<?> search(NoticeDto.Search rq) {

        // [1] 요청
        Page<NoticeDto.Row> rp = noticeService.search(rq);

        // [2] 페이징 응답 전달
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }

    /**
     * 공지사항 상세 조회 API
     * [GET] /admin/api/notices/{noticeId}
     */

    @GetMapping("/{noticeId}")
    public ResponseEntity<?> getDetail(@PathVariable Long noticeId) {
        // [1] 상세 조회 요청
        NoticeDto.Detail detail = noticeService.getDetail(noticeId);

        // [2] 데이터 반환
        return Api.ok(detail);
    }

    /**
     * 공지사항 수정
     * [PUT] /admin/api/notices/{noticeId}
     */

    @PutMapping("/{noticeId}")
    public ResponseEntity<?> editDetail(@PathVariable Long noticeId,
                                        @RequestBody NoticeDto.Edit rq) {
        rq.setNoticeId(noticeId);
        NoticeDto.Detail rp = noticeService.edit(rq);

        return Api.ok(rp);
    }


    /**
     * 공지사항 삭제
     * [DELETE] /admin/api/notices/{noticeId}
     */

    @DeleteMapping("/{noticeId}")
    public ResponseEntity<?> removeDetail(@PathVariable Long noticeId) {
        noticeService.remove(noticeId);
        return Api.ok();
    }

    /**
     * 공지사항 추가
     * [POST] /admin/api/notices/
     */
    @PostMapping
    public ResponseEntity<?> addNotice(@RequestBody NoticeDto.Write rq) {
        NoticeDto.Detail saved = noticeService.write(rq);
        return Api.ok(saved);
    }
}
