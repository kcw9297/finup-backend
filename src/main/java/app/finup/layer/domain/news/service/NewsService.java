package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;
import java.util.Map;

/**
 * 뉴스 로직처리 서비스 인터페이스
 * @author oyh
 * @since 2025-12-01
 */
public interface NewsService {
    List<NewsDto.Row> getNews(String category);
    void refreshCategory(String category);
    void refreshAllCategories();
}
