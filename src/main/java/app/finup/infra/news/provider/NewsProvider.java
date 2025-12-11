package app.finup.infra.news.provider;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;

public interface NewsProvider {
    List<NewsDto.Row> getNews(String category, int limit);
    List<NewsDto.Row> fetchNews(String category, int limit);
    List<NewsDto.Row> getStockNews(String keyword, String category, int limit);
    List<NewsDto.Row> fetchStockNews(String keyword, String category, int limit);

}
