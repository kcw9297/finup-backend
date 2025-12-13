package app.finup.layer.domain.words.controller;

import app.finup.common.constant.Url;
import app.finup.common.enums.AppStatus;
import app.finup.common.utils.Api;
import app.finup.layer.domain.words.service.WordsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


/**
 * 용어사전 REST API 요청 후 불러오는 컨트롤러 클래스 (일반적으로 DB 비었을 때 단 한번만 실행)
 * (+ 웹 스크래퍼 포함)
 * @author khj
 * @since 2025-12-10
 */


@Slf4j
@RestController
@RequestMapping(Url.ADMIN_WORDS)
@RequiredArgsConstructor
public class AdminWordsController {

    private final WordsService financeDictionaryService;

    /**
     * 금융 용어 초기 적재 (1회 전용)
     * [POST] /admin/api/dict/init
     */

    @PostMapping("/init")
    public ResponseEntity<?> initDictionary() {
        if (financeDictionaryService.isInitialized()) {
            return Api.fail(AppStatus.FINANCE_DICT_ALREADY_INITIALIZED);
        }

        financeDictionaryService.refreshTerms();
        return Api.ok("금융 용어 사전 초기 적재 완료");
    }

    /**
     * KBThink 전체 크롤링 후 DB 반영
     * [POST] /admin/api/dict/kbthink/init
     */
    @PostMapping("/kbthink/init")
    public ResponseEntity<?> initFromKbThink() {
        log.info("▶ KBThink Crawl API 호출됨!");
        financeDictionaryService.crawlAllFromKbThink();
        return Api.ok("KBThink 용어 전체 수집 완료");
    }
}
