package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

public interface StockNewsAiService {
    public NewsDto.Summary analyzeLightCached(String link, String description);
}
