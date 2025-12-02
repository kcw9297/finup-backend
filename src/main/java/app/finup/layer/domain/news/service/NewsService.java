package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;

public interface NewsService {
    List<NewsDto.Row> getNews(int page, String keyword, String sort);

}
