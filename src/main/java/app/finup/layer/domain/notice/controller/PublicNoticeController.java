package app.finup.layer.domain.notice.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.notice.dto.NoticeDto;
import app.finup.layer.domain.notice.service.NoticeService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 공개용 공지사항 컨트롤러 클래스
 * @author kcw
 * @since 2025-12-15
 */

@Slf4j
@RestController
@RequestMapping(Url.NOTICE_PUBLIC)
@RequiredArgsConstructor
public class PublicNoticeController {

    private final NoticeService noticeService;

    /**
     * 게시글 검색
     * [GET] /notices/search
     * @param rq 게시글 검색 요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(NoticeDto.Search rq) {

        // [1] 요청
        Page<NoticeDto.Row> rp = noticeService.getPagedList(rq);

        // [2] 페이징 응답 전달
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }


    /**
     * 페이지 홈에 게시할 공지사항 목록
     * [GET] /notices/home
     */
    @GetMapping("/home")
    public ResponseEntity<?> getHomeList() {
        return Api.ok(noticeService.getHomeList());
    }


    /**
     * 공지사항 상세 조회
     * [GET] /notices/{noticeId}
     */

    @GetMapping("/{noticeId:[0-9]+}")
    public ResponseEntity<?> getDetail(@PathVariable Long noticeId) {

        // [1] 상세 조회 요청
        NoticeDto.Detail detail = noticeService.getDetail(noticeId);

        // [2] 데이터 반환
        return Api.ok(detail);
    }


}
