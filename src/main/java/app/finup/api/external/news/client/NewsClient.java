package app.finup.api.external.news.client;

import app.finup.api.external.news.dto.NewsApi;

import java.util.List;

/**
 * 뉴스 API 호출 기능 제공 Provider 인터페이스
 * @author kcw
 * @since 2025-12-24
 */

public interface NewsClient {

    /**
     * 뉴스 API - 가장 최근 뉴스목록 조회
     * @param query  검색어 (쿼리)
     * @param amount 검색 기사 최대 수량
     * @return API 요청 결과 DTO 리스트
     */
    List<NewsApi.Row> getLatest(String query, int amount);
}
