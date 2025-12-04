package app.finup.layer.domain.news.service;

import app.finup.infra.ai.AiManager;
import app.finup.infra.ai.PromptTemplates;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import com.fasterxml.jackson.core.type.TypeReference;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;

import java.time.Duration;
import java.util.Comparator;
import java.util.List;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class NewsAiServiceImpl implements NewsAiService {

    private final AiManager aiManager;
    private final NewsRedisStorage newsRedisStorage;

    private static final Duration DURATION_AI = Duration.ofHours(6);
    @Override
    public Map<String, Object> getNewsAi(String url) {

        String hash = DigestUtils.md5DigestAsHex(url.getBytes());
        String key = "NEWS_AI:" + hash;

        //조회
        Map<String, Object> cashAi = newsRedisStorage.getNews(key, new TypeReference<>() {});
        if(cashAi != null) {
            log.debug("[NEWS_AI] HIT {}", url);
            return cashAi;
        }
        //없으면 ai실행
        String article = extractArticle(url);
        String prompt = PromptTemplates.NEWS_ANALYSIS.replace("{ARTICLE}", article);
        Map<String, Object> freshAi= aiManager.runJsonPrompt(prompt);

        //키워드 정렬
        List<Map<String, String>> keywords = (List<Map<String, String>>) freshAi.get("keywords");
        if(keywords != null){
            keywords.sort(Comparator.comparing(k -> k.get("term")));
            freshAi.put("keywords", keywords);
        }
        newsRedisStorage.saveNews(key, freshAi, DURATION_AI);

        return freshAi;
    }

    @Override
    public String extractArticle(String url) {
        try {
            // User-Agent 지정 중요 (언론사 일부 bot 차단 있음)
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64)")
                    .timeout(5000)
                    .get();

            // 언론사별 본문 CSS 선택자 시도
            String content = tryExtractContent(doc);

            return content;

        } catch (Exception e) {
            e.printStackTrace();
            return ""; // 오류 시 빈 문자열
        }
    }

    private String tryExtractContent(Document doc) {
        Element content = doc.selectFirst("dic_area");
        if(content != null) return content.text();

        return "";
    }


}
