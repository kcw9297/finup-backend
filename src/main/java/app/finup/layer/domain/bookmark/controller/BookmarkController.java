package app.finup.layer.domain.bookmark.controller;


import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.bookmark.dto.BookmarkDto;
import app.finup.layer.domain.bookmark.enums.BookmarkTarget;
import app.finup.layer.domain.bookmark.service.BookmarkService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

/**
 * 북마크 공개용 REST API 클래스
 * @author kcw
 * @since 2025-12-07
 */

@Slf4j
@RestController
@RequestMapping(Url.BOOKMARK)
@RequiredArgsConstructor
public class BookmarkController {

    private final BookmarkService bookmarkService;

    /**
     * 현재 로그인 회원의 북마크 목록 조회
     * [GET] /bookmarks
     */
    @GetMapping
    public ResponseEntity<?> getMyList(@AuthenticationPrincipal CustomUserDetails userDetails) {

        Long memberId = userDetails.getMemberId();
        return Api.ok(bookmarkService.getMyList(memberId));
    }


    /**
     * 북마크 등록
     * [POST] /bookmarks
     * @param rq 북마크 등록요청 DTO
     */
    @PostMapping
    public ResponseEntity<?> add(@AuthenticationPrincipal CustomUserDetails userDetails,
                                 @RequestBody BookmarkDto.Add rq) {

        // [1] 북마크 등록
        bookmarkService.add(rq);

        // [2] 성공 응답 반환
        return Api.ok();
    }


    /**
     * 북마크 삭제
     * [DELETE] /bookmarks
     */
    @DeleteMapping
    public ResponseEntity<?> remove(@RequestParam Long targetId,
                                    @RequestParam BookmarkTarget bookmarkTarget) {

        // [1] 북마크 삭제
        bookmarkService.remove(targetId, bookmarkTarget);

        // [2] 성공 응답 반환
        return Api.ok();
    }

}
