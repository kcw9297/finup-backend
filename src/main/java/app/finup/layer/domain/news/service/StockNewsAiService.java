package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

public interface StockNewsAiService {
    Object analyzeAuto(String link, String description);
    NewsDto.Summary analyzeLightCached(String link, String description);
    NewsDto.Ai analyzeDeepCached(String link);
}
