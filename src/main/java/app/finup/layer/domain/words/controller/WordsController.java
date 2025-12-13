package app.finup.layer.domain.words.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.words.dto.WordsDto;
import app.finup.layer.domain.words.service.WordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

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
    public ResponseEntity<?> search(WordsDto.Search rq) {
        // [1] 요청
        Page<WordsDto.Row> rp = wordsService.search(rq);
        // [2] 페이징 응답 전달
        return Api.ok(rp.getRows(), Pagination.of(rp));
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
