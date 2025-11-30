package app.finup.layer.domain.reboard.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.reboard.dto.ReboardDto;
import app.finup.layer.domain.reboard.service.ReboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 REST API 클래스
 * @author kcw
 * @since 2025-11-24
 */

@Slf4j
@RestController
@RequestMapping(Url.REBOARD_ADMIN)
@RequiredArgsConstructor
public class ReboardController {

    private final ReboardService reboardService;

    /**
     * 게시글 작성 API
     * [POST] /reboards
     * @param rq 게시글 작성 요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> write(@RequestBody ReboardDto.Write rq) {

        // [1] 생성 요청
        Long idx = reboardService.write(rq);

        // [2] 성공 응답
        return Api.ok(AppStatus.REBOARD_OK_WRITE, idx);
    }

    /**
     * 게시글 수정 API
     * [POST] /reboards/{idx}
     * @param idx 대상 게시글 번호
     * @param rq 게시글 수정 요청 DTO
     */
    @PutMapping("/{idx:[0-9]+}")
    public ResponseEntity<?> edit(@PathVariable Long idx,
                                  @RequestBody ReboardDto.Edit rq) {

        // [1] 갱신 요청
        rq.setIdx(idx);
        reboardService.edit(rq);

        // [2] 성공 응답 반환
        return Api.ok(AppStatus.REBOARD_OK_EDIT);
    }

    /**
     * 게시글 삭제 API
     * [DELETE] /reboards/{idx}
     * @param idx 대상 게시글 번호
     */
    @DeleteMapping("/{idx:[0-9]+}")
    public ResponseEntity<?> remove(@PathVariable Long idx) {
        reboardService.remove(idx);
        return Api.ok();
    }

}