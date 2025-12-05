package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.List;
/**
 * 뉴스 중복제거 처리 서비스 인터페이스
 * @author oyh
 * @since 2025-12-05
 */
public interface NewsRemoveDuplicateService {
    List<NewsDto.Row> removeDuplicate(List<NewsDto.Row> list);
}
