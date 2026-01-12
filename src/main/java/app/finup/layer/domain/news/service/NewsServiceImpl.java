package app.finup.layer.domain.news.service;

import app.finup.infra.api.news.provider.NewsProvider;
import app.finup.layer.domain.news.component.NewsContentExtractor;
import app.finup.layer.domain.news.dto.NewsDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
/**
 * NewsService 구현 클래스
 * @author oyh
 * @since 2025-12-01
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class NewsServiceImpl implements NewsService {
    private final NewsProvider newsProvider;

    /**
     * 프론트에서 호출하는 메인 메서드
     * GET /news/list?category=date
     */
    @Override
    public List<NewsDto.Row> getNews(String category) {
        return newsProvider.getNews(category, 50);
    }

    @Override
    public List<NewsDto.Row> getLatestNews(String category, int limit) {
        List<NewsDto.Row> list = newsProvider.getNews(category, 50);
        if(list == null || list.isEmpty()){
            return List.of();
        }
        int toIndex = Math.min(limit, list.size());
        return list.subList(0, toIndex);
    }

}
