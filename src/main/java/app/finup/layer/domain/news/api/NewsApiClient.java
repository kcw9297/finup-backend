package app.finup.layer.domain.news.api;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;
/**
 * Naver API 호출 및 Json파싱 인터페이스
 * @author oyh
 * @since 2025-12-01
 */
public interface NewsApiClient {

    List<NewsDto.Row> fetchNews(String query,String sort, int display);

}
