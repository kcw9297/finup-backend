package app.finup.layer.domain.memberWordbook.controller;

import app.finup.common.constant.Url;
import app.finup.common.utils.Api;
import app.finup.layer.domain.memberWordbook.repository.WordViewRepository;
import app.finup.layer.domain.memberWordbook.service.WordViewService;
import app.finup.layer.domain.words.service.WordsService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * 최근 본 단어 REST API
 * @author khj
 * @since 2025-12-14
 */


@RestController
@RequiredArgsConstructor
@RequestMapping(Url.MEMBER_WORD_VIEW)
public class WordViewController {

    private final WordViewService wordViewService;

    /**
     * 내 최근 본 단어 목록 조회
     * [GET] /api/members/wordbook/view
     */
    @GetMapping
    public ResponseEntity<?> getMyRecentWords() {
        return Api.ok(wordViewService.getMyRecentWords());
    }

}
