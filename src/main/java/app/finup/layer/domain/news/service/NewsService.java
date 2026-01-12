package app.finup.layer.domain.news.service;

import app.finup.common.dto.Page;
import app.finup.layer.domain.news.dto.NewsDto;

/**
 * 최신 뉴스 정보와 관련한 비즈니스 로직 제공 Service 인터페이스
 * @author kcw
 * @since 2025-12-24
 */

public interface NewsService {

    /**
     * 메인 기사 목록 조회
     * @param pageNum 현재 페이지
     * @param pageSize 페이징 사이즈
     * @return 페이징된 뉴스 DTO 목록
     */
    Page<NewsDto.Row> getPagedMainNewsList(int pageNum, int pageSize);


    /**
     * 특정 종목 뉴스 기사 목록 조회
     * @param stockCode 대상 종목 코드
     * @param pageNum 현재 페이지
     * @param pageSize 페이징 사이즈
     * @return 페이징된 뉴스 DTO 목록
     */
    Page<NewsDto.Row> getPagedStockNewsList(String stockCode, int pageNum, int pageSize);

}
