package app.finup.layer.domain.news.service;

import app.finup.layer.domain.news.dto.NewsDto;

import java.util.Map;

public interface NewsAiService {
//    NewsDto.Ai analyze(String url);
    NewsDto.Ai analyzeDeep(String url);
    NewsDto.Summary analyzeLight(String summary);
}
