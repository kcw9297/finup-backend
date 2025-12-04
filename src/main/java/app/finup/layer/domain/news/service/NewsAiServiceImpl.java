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

        // 연합뉴스
        Element yna = doc.selectFirst("article#articleWrap");
        if (yna != null) return yna.text();

        // 한국경제
        Element hk = doc.selectFirst("div#articletxt, div.article-body");
        if (hk != null) return hk.text();

        // 조선비즈
        Element cbiz = doc.selectFirst("div#news_body_id, div.article-body");
        if (cbiz != null) return cbiz.text();

        // 매일경제
        Element mk = doc.selectFirst("div#article_body, div#article_body_id, section.article");
        if (mk != null) return mk.text();

        // 머니투데이
        Element mt = doc.selectFirst("div#article, div#textBody");
        if (mt != null) return mt.text();

        // 아시아경제
        Element asiae = doc.selectFirst("div#articleBody, div#txt_content");
        if (asiae != null) return asiae.text();

        // 뉴시스
        Element newsis = doc.selectFirst("div#content, div.viewBox");
        if (newsis != null) return newsis.text();

        // 파이낸셜뉴스
        Element fn = doc.selectFirst("div#article_content, div#article_body");
        if (fn != null) return fn.text();

        // 디지털타임스
        Element dt = doc.selectFirst("div#articleBody, div.article_txt");
        if (dt != null) return dt.text();

        // 전자신문
        Element et = doc.selectFirst("div#articleBody, div#articleTxt");
        if (et != null) return et.text();

        // 헤럴드경제
        Element herald = doc.selectFirst("div#articleText, div.article-text");
        if (herald != null) return herald.text();

        // 그 외 기본적인 기사 body 후보들
        Element generic = doc.selectFirst("article, div.article, div#content, div.story-body, section");
        if (generic != null) return generic.text();

        return "";
    }


}
