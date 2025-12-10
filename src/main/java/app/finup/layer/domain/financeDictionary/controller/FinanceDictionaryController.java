package app.finup.layer.domain.financeDictionary.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.financeDictionary.dto.FinanceDictionaryDto;
import app.finup.layer.domain.financeDictionary.service.FinanceDictionaryService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * DB에 저장된 용어 사전 내역 관련 컨트롤러 클래스
 * @author khj
 * @since 2025-12-10
 */


@Slf4j
@RestController
@RequestMapping(Url.DICTIONARY)
@RequiredArgsConstructor
public class FinanceDictionaryController {

    private final FinanceDictionaryService financeDictionaryService;


    /**
     * 단어 검색 API
     * [GET] /api/dict/search
     * @param rq 게시글 검색 요청 DTO
     */

    @GetMapping("/search")
    public ResponseEntity<?> search(FinanceDictionaryDto.Search rq) {
        // [1] 요청
        Page<FinanceDictionaryDto.Row> rp = financeDictionaryService.search(rq);
        // [2] 페이징 응답 전달
        return Api.ok(rp.getRows(), Pagination.of(rp));
    }
}
