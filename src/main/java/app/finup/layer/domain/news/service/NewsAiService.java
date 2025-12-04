package app.finup.layer.domain.news.service;

import java.util.Map;

public interface NewsAiService {
    Map<String, Object> getNewsAi(String article);
    String extractArticle(String url);
}
