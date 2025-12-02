package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;
import java.util.Map;

public interface NewsService {
    String extractArticle(String url);
    List<NewsDto.Row> getNews(int page, String keyword, String sort);
}
