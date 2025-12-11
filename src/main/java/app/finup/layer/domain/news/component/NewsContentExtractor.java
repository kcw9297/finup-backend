package app.finup.layer.domain.news.component;

import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Component;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import java.util.List;

@Slf4j
@Component
public class NewsContentExtractor {
    /** 뉴스 URL이 AI 분석 가능한 매체인지 확인 */
    public boolean isSupported(String url) {
        try {
            String article = extract(url);

            // 본문이 100자 이상이면 “추출 성공”
            return article != null && article.trim().length() > 100;
        } catch (Exception e) {
            return false;
        }
    }

    /** 도메인에 맞는 본문 추출 실행 */
    public String extract(String url) {
        try {
            Document doc = Jsoup.connect(url)
                    .userAgent("Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 "
                            + "(KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36")
                    .referrer("https://www.google.com")
                    .timeout(5000)
                    .get();

            // 네이버 뉴스 전용 (가장 우선)
            Element naver = doc.selectFirst("#dic_area, .newsct_article, .go_trans._article_content");
            if (naver != null) {
                String txt = cleanText(naver.text());
                if (txt.length() > 50) return txt;
            }

            // 네이버 모바일 전용
            Element mobileNaver = doc.selectFirst("#dic_area, .media_end_body, #newsct_article, #content, .newsct_article");
            if (mobileNaver != null) {
                String txt = cleanText(mobileNaver.text());
                if (txt.length() > 50) return txt;
            }

            // 국내 언론사 공통 article 태그
            Element article = doc.selectFirst("article");
            if (article != null) {
                String txt = cleanText(article.text());
                if (txt.length() > 50) return txt;
            }

            // 자주 쓰는 본문 영역
            String[] commonSelectors = {
                    "#content", "#news_body", "#articleBody", ".article-body",
                    ".article-content", ".art_txt", ".content-wrapper",
                    ".news_article", ".read_txt", ".view_cont, #, .nodeContentTitle, .news_body, .content-area"
            };

            for (String selector : commonSelectors) {
                Element el = doc.selectFirst(selector);
                if (el != null) {
                    String txt = cleanText(el.text());
                    if (txt.length() > 50) return txt;
                }
            }

            // 4) fallback – 페이지 전체 텍스트 중 가장 본문 같은 영역 선택
            Element body = doc.body();
            String fallback = cleanText(body.text());

            if (fallback.length() > 100) {
                log.info("[EXTRACTOR] fallback 사용 url={}", url);
                return fallback;
            }

            log.warn("[EXTRACTOR] 본문 추출 실패 url={}", url);
            return "";

        } catch (Exception e) {
            log.warn("[EXTRACTOR] Jsoup 실패 url={}", url);
            return "";
        }

    }
    private String cleanText(String text) {
        if (text == null) return "";

        return text
                .replaceAll("\\s+", " ")
                .replaceAll("▶.*$", "")
                .replaceAll("ⓒ.*$", "")
                .replaceAll("무단 전재.*", "")
                .replaceAll("기자]", "")
                .trim();
    }



}
