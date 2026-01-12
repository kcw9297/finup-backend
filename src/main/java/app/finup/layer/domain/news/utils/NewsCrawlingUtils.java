package app.finup.layer.domain.news.utils;

import app.finup.common.utils.LogUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

import java.io.IOException;
import java.time.Duration;
import java.util.List;
import java.util.Objects;

/**
 * 뉴스와 관련한 유틸 기능 클래스
 * @author kcw
 * @since 2026-01-07
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsCrawlingUtils {

    // HTML 검색 요청 상수
    private static final String ALT = "alt";
    private static final String CONTENT = "content";
    private static final String USER_AGENT_BASE = "Mozilla/5.0";
    private static final String USER_AGENT_CONTENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    private static final String URL_GOOGLE = "https://www.google.com";
    private static final Duration TIMEOUT = Duration.ofSeconds(5);
    private static final int THRESHOLD_LENGTH = 50;
    private static final String DEFAULT_PUBLISHER = "네이버뉴스";

    // 각 사이트마다 사용하는 selector
    private static final String SELECTORS_NAVER_NEWS = "#dic_area, .newsct_article, .go_trans._article_content";
    private static final String SELECTORS_NAVER_NEWS_MOBILE = "#dic_area, .media_end_body, #newsct_article, #content, .newsct_article";
    private static final String SELECTORS_ARTICLE = "article";
    private static final String SELECTORS_ETC = "#content, #news_body, #articleBody, .article-body, .article-content, .art_txt, .content-wrapper, .news_article, .read_txt, .view_cont, .nodeContentTitle, .news_body, .content-area";

    // 이미지, 작성자를 추출하기 위한 selector
    private static final String SELECTOR_IMAGE = "meta[property=og:image]";
    private static final String SELECTOR_IMAGE_SITE = "meta[property=og:site_name]";
    private static final String SELECTOR_IMAGE_LOGO = ".media_end_head_top_logo_img";
    private static final String SELECTOR_TEXT_LOGO = ".press_logo";


    /**
     * 뉴스 본문 추출 (크롤링)
     * @param newsUrl 뉴스 URL
     * @return 추출한 뉴스 본문
     */
    public static String extractDescription(String newsUrl) {

        try {
            // [1] Chrome 브라우저에서 접근한 뉴스 링크 내 HTML 정보 추출
            Document html = connectToUrl(newsUrl, USER_AGENT_CONTENT);

            // [2] 네이버 뉴스 -> 네이버 모바일 뉴스 -> 언론사 홈페이지 -> 그 외 사이트 순으로 크롤링 수행
            // 정상 추출에 성공한 경우 반환 (50줄 이상의 내용 추출에 성공)
            List<String> newsSelectors = List.of(SELECTORS_NAVER_NEWS, SELECTORS_NAVER_NEWS_MOBILE, SELECTORS_ARTICLE, SELECTORS_ETC);
            for (String selector : newsSelectors) {

                Element element = html.selectFirst(selector); // 본문 내용 추출

                // 만약 크롤링 결과가 존재하면 추출 후 반환
                if (Objects.nonNull(element)){
                    String content = cleanText(element.text());
                    if (content.length() >= THRESHOLD_LENGTH) return content;
                }
            }

            // [3] 만약 위의 SELECTOR로도 추출에 실패하는 경우, 로그를 남기고 빈 문자열 반환
            LogUtils.showWarn(NewsCrawlingUtils.class, "본문 추출 실패! 추출 시도 기사 URL : %s", newsUrl);
            return "";

            // 예외 발생 시, 로그를 남기고 빈 문자열 반환
        } catch (Exception e) {
            LogUtils.showWarn(NewsCrawlingUtils.class, "본문 추출 시도 중 예외 발생! 추출 시도 기사 URL : %s, 예외 메세지 : %s", newsUrl, e.getMessage());
            return "";
        }
    }


    /**
     * 뉴스 썸네일 이미지 추출 (크롤링)
     * @param newsUrl 뉴스 URL
     * @return 추출한 뉴스 썸네일 이미지 URL
     */
    public static String extractThumbnail(String newsUrl) {

        try {
            // [1] 뉴스 링크 접근
            Document doc = connectToUrl(newsUrl, USER_AGENT_BASE);

            // [2] 이미지 추출 후 반환
            return doc.select(SELECTOR_IMAGE).attr(CONTENT);

        } catch (Exception e) {
            LogUtils.showWarn(NewsCrawlingUtils.class, "뉴스 섬네일 이미지 추출 실패! 추출 시도 기사 URL : %s, 예외 메세지 : %s", newsUrl, e.getMessage());
            return "";
        }
    }


    /**
     * 뉴스 퍼블리셔 (언론사 정보) 정보 추출 (크롤링)
     * @param newsUrl 뉴스 URL
     * @return 추출한 뉴스 퍼블리셔 정보
     */
    public static String extractPublisher(String newsUrl) {

        try {
            // [1] 뉴스 링크 접근
            Document doc = connectToUrl(newsUrl, USER_AGENT_BASE);

            // [2] 작성자 추출 후 반환
            // og:site_name 사용
            String press = doc.select(SELECTOR_IMAGE_SITE).attr(CONTENT);
            if (!press.isBlank()) return press;

            // 이미지 로고 alt
            press = doc.select(SELECTOR_IMAGE_LOGO).attr(ALT);
            if (!press.isBlank()) return press;

            // 텍스트 로고
            press = doc.select(SELECTOR_TEXT_LOGO).text();
            if (!press.isBlank()) return press;

            // [3] 모두 추출 실패 시, 기본 작성자 반환
            return DEFAULT_PUBLISHER;


        } catch (Exception e) {
            LogUtils.showWarn(NewsCrawlingUtils.class, "뉴스 작성자 정보 추출 실패! 추출 시도 기사 URL : %s, 예외 메세지 : %s", newsUrl, e.getMessage());
            return "";
        }
    }


    // 뉴스에 불필요한 텍스트 제거
    private static String cleanText(String text) {

        return Objects.isNull(text) ?
                "" :
                text.replaceAll("\\s+", " ")
                        .replaceAll("▶.*$", "")
                        .replaceAll("ⓒ.*$", "")
                        .replaceAll("무단 전재.*", "")
                        .replaceAll("기자]", "")
                        .trim();
    }


    // 뉴스 URL 기반 크롤 환경으로 접속
    private static Document connectToUrl(String url, String userAgent) throws IOException {
        return Jsoup.connect(url)
                .userAgent(userAgent)
                .referrer(URL_GOOGLE) // 구글에서 검색해서 온 것 처럼 취급
                .timeout((int) TIMEOUT.toMillis())
                .get();
    }

}
