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

/**
 * 관리자 공지사항 컨트롤러 클래스
 * @author khj
 * @since 2025-12-01
 */

@Slf4j
@RestController
@RequestMapping(Url.NOTICE_ADMIN)
@RequiredArgsConstructor
public class AdminNoticeController {

    private final NoticeService noticeService;


    /**
     * 공지사항 추가
     * [POST] /notices
     * @param rq 공지사항 작성 요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> write(@RequestBody @Validated NoticeDto.Write rq) {

        // [1] 공지사항 작성
        noticeService.write(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.NOTICE_OK_WRITE);
    }


    /**
     * 공지사항 수정
     * [PUT] /notices/{noticeId}
     */

    @PutMapping("/{noticeId:[0-9]+}")
    public ResponseEntity<?> edit(@PathVariable Long noticeId,
                                  @RequestBody @Validated NoticeDto.Edit rq) {

        // [1] 갱신 수행
        rq.setNoticeId(noticeId);
        noticeService.edit(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.NOTICE_OK_EDIT);
    }


    /**
     * 공지사항 삭제
     * [DELETE] /notices/{noticeId}
     */

    @DeleteMapping("/{noticeId:[0-9]+}")
    public ResponseEntity<?> remove(@PathVariable Long noticeId) {

        // [1] 삭제 수행
        noticeService.remove(noticeId);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.NOTICE_OK_REMOVE);
    }
}
