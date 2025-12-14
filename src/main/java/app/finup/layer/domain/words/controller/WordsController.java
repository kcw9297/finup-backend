package app.finup.layer.domain.words.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.auth.dto.AuthDto;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.service.WordsService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * DB에 저장된 용어 사전 내역 관련 컨트롤러 클래스
 * @author khj
 * @since 2025-12-10
 */


@Slf4j
@RestController
@RequestMapping(Url.WORDS)
@RequiredArgsConstructor
public class WordsController {

    private final WordsService wordsService;


    /**
     * 단어 검색 API
     * [GET] /api/words/search
     * @param rq 게시글 검색 요청 DTO
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            WordsDto.Search rq,
            @AuthenticationPrincipal CustomUserDetails user) {

        // [0] 유저 검증
        Long memberId = (user != null ? user.getMemberId() : null);
        log.info("[SEARCH] principal={}", user);

        // [1] 요청
        Page<WordsDto.Row> rp = wordsService.search(rq, memberId);
        // [2] 페이징 응답 전달
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }

    @GetMapping("/recent-searches")
    public ResponseEntity<?> getRecentSearches(@AuthenticationPrincipal CustomUserDetails user) {

        List<String> list = wordsService.getRecent(user.getMemberId());
        log.info("[RECENT-SEARCH] principal={}", user);

        return Api.ok(list);
    }

    /**
     * 최근 검색어 단건 삭제
     * [DELETE] /api/words/recent-searches/{keyword}
     */
    @DeleteMapping("/recent-searches/{keyword}")
    public ResponseEntity<?> removeRecent(
            @PathVariable String keyword,
            @AuthenticationPrincipal CustomUserDetails user
    ) {
        wordsService.removeRecent(user.getMemberId(), keyword);
        return Api.ok();
    }


    /**
     * 단어장 홈 관련 API
     * [GET] /api/words/home
     */

    @GetMapping("/home")
    public ResponseEntity<?> home() {
        return Api.ok(wordsService.getHomeWords());
    }
}
