package app.finup.layer.domain.words.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.base.validation.annotation.Search;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.service.WordsService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Objects;

/**
 * DB에 저장된 용어 사전 내역 관련 컨트롤러 클래스
 * @author khj
 * @since 2025-12-10
 */


@Slf4j
@RestController
@RequestMapping(Url.WORDS)
@RequiredArgsConstructor
@Validated
public class WordsController {

    private final WordsService wordsService;


    /**
     * 단어 검색 API
     * [GET] /words/search
     */
    @GetMapping("/search")
    public ResponseEntity<?> search(
            @Search @RequestParam String keyword,
            @AuthenticationPrincipal CustomUserDetails userDetails) {

        // [1] 검색 수행
        String trimmed = Objects.isNull(keyword) ? "" : keyword.trim();
        List<WordsDto.Row> rows = wordsService.search(trimmed, userDetails.getMemberId());

        // [2] 검색 단어 저장 (비지 않은 경우)
        if (!rows.isEmpty()) wordsService.storeRecentWord(userDetails.getMemberId(), trimmed);

        // [3] 검색 결과 반환
        return Api.ok(rows);
    }


    /**
     * 접속 회원 최근 단어 조회 API
     * [GET] /words/recent-searches
     */
    @GetMapping("/recent-searches")
    public ResponseEntity<?> getRecentSearches(@AuthenticationPrincipal CustomUserDetails userDetails) {
        return Api.ok(wordsService.getRecent(userDetails.getMemberId()));
    }


    /**
     * 최근 검색어 단건 삭제
     * [DELETE] /words/recent-searches/{keyword}
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


    /**
     * 단어 상세 조회 API
     * [GET] /words/detail/{termId}
     */

    @GetMapping("/detail/{termId:[0-9]+}")
    public ResponseEntity<?> getDetail(@PathVariable Long termId) {
        return Api.ok(wordsService.getDetail(termId));
    }

}
