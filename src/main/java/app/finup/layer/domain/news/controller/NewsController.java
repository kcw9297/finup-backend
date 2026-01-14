package app.finup.layer.domain.news.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.service.NewsAiService;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@Slf4j
@RequestMapping(Url.NEWS_PUBLIC)
@RestController
@RequiredArgsConstructor
public class NewsController {

    // 사용 의존성
    private final NewsAiService newsAiService;

    /**
     * 최근 순으로 메인 뉴스 조회 (무한 스크롤)
     * [GET] /news/main
     */
    @GetMapping("/{newsId}/analysis")
    public ResponseEntity<?> getAnalysis(@AuthenticationPrincipal CustomUserDetails userDetails,
                                         @PathVariable Long newsId,
                                         @RequestParam(defaultValue = "false") boolean retry) {

        return retry ?
                Api.ok(newsAiService.retryAnalyze(newsId, userDetails.getMemberId())) :
                Api.ok(newsAiService.analyze(newsId, userDetails.getMemberId()));
    }

}
