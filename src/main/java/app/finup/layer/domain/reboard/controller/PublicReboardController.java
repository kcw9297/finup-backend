package app.finup.layer.domain.reboard.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.reboard.dto.ReboardDto;
import app.finup.layer.domain.reboard.service.ReboardService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 게시글 공개용 REST API 클래스
 * @author kcw
 * @since 2025-11-24
 */

@Slf4j
@RestController
@RequestMapping(Url.REBOARD_PUBLIC)
@RequiredArgsConstructor
public class PublicReboardController {

    private final ReboardService reboardService;

    /**
     * 게시글 검색 API
     * [GET] /reboards/search
     * @param rq 게시글 검색 요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(ReboardDto.Search rq) {

        // [1] 요청
        Page<ReboardDto.Row> rp = reboardService.search(rq);

        // [2] 페이징 응답 전달
        return Api.ok(rp.getList(), Pagination.of(rp));
    }


    /**
     * 게시글 상세 조회 API
     * [GET] /reboards/{idx}
     * @param idx 대상 게시글 번호
     */
    @GetMapping("/{idx:[0-9]+}")
    public ResponseEntity<?> getDetail(@PathVariable Long idx) {
        return Api.ok(reboardService.getDetail(idx));
    }


    // 테스트용 전체 조회
    @GetMapping
    public ResponseEntity<?> getAll() {
        return Api.ok(reboardService.getList());
    }

}