package app.finup.layer.domain.stockNews.service;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;

/**
 * 종목 관련 뉴스 서비스 인터페이스
 * @author lky
 * @since 2025-12-03
 */
public interface StockNewsService {
    List<NewsDto.Row> getNews(String category, String stockName);
    void refreshCategory(String category, String stockName);
    void refreshAllCategories(String stockName);
}
