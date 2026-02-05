package app.finup.layer.domain.news.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.service.NewsAiService;
import app.finup.layer.domain.news.service.NewsService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(Url.NEWS)
@RestController
@RequiredArgsConstructor
public class NewsController {

    // 사용 의존성
    private final NewsAiService newsAiService;
    private final NewsService newsService;

    /**
     * 뉴스 분석 API
     * [GET] /news/{newsId}/analysis
     */
    @GetMapping("/{newsId:[0-9]+}/analysis")
    public ResponseEntity<?> getAnalysis(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Long newsId,
                                         @RequestParam(defaultValue = "false") boolean retry) {

        return retry ?
                Api.ok(newsAiService.retryAndGetAnalyze(newsId, userDetails.getMemberId())) :
                Api.ok(newsAiService.getAnalysis(newsId, userDetails.getMemberId()));
    }


    /**
     * 뉴스 내 단어 분석 API
     * [GET] /words/recommendation/news/{newsId}
     */
    @GetMapping("/{newsId:[0-9]+}/analysis/words")
    public ResponseEntity<?> getAnalysisWords(
            @AuthenticationPrincipal CustomUserDetails userDetails,
            @PathVariable Long newsId,
            @RequestParam boolean retry) {

        // 현재 요청 회원번호
        Long memberId = userDetails.getMemberId();

        // 재시도 여부에 따라 분기 처리
        return retry ?
                Api.ok(newsAiService.retryAndGetAnalysisWords(newsId, memberId)) :
                Api.ok(newsAiService.getAnalysisWords(newsId, memberId));
    }


    /**
     * 최근 순으로 종목 뉴스 조회 (무한 스크롤)
     * [GET] /news/stock
     */
    @GetMapping("/stock")
    public ResponseEntity<?> getPagedStockNewsList(@RequestParam String stockCode,
                                                   @RequestParam int pageNum,
                                                   @RequestParam int pageSize) {

        // [1] 페이징 수행
        Page<NewsDto.Row> page = newsService.getPagedStockNewsList(stockCode, pageNum, pageSize);

        // [2] 페이징 결과 반환
        return Api.ok(page.getRows(), Pagination.of(page));
    }


}
