package app.finup.layer.domain.news.controller;

import app.finup.common.constant.Url;
import app.finup.common.dto.Page;
import app.finup.common.dto.Pagination;
import app.finup.common.utils.Api;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.service.NewsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RequestMapping(Url.NEWS_PUBLIC)
@RestController
@RequiredArgsConstructor
public class PublicNewsController {

    // 사용 의존성
    private final NewsService newsService;

    /**
     * 최근 순으로 메인 뉴스 조회 (무한 스크롤)
     * [GET] /news/main
     */
    @GetMapping("/main")
    public ResponseEntity<?> getPagedMainNewsList(int pageNum, int pageSize) {

        // [1] 페이징 수행
        Page<NewsDto.Row> page = newsService.getPagedMainNewsList(pageNum, pageSize);

        // [2] 페이징 결과 반환
        return Api.ok(page.getRows(), Pagination.of(page));
    }


    /**
     * 최근 순으로 종목 뉴스 조회 (무한 스크롤)
     * [GET] /news/stock
     */
    @GetMapping("/stock")
    public ResponseEntity<?> getPagedStockNewsList(String stockCode, int pageNum, int pageSize) {

        // [1] 페이징 수행
        Page<NewsDto.Row> page = newsService.getPagedStockNewsList(stockCode, pageNum, pageSize);

        // [2] 페이징 결과 반환
        return Api.ok(page.getRows(), Pagination.of(page));
    }
}
