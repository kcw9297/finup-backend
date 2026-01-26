package app.finup.layer.domain.news.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.news.dto.NewsDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.safety.Safelist;

import java.io.IOException;
import java.time.Duration;
import java.util.*;
import java.util.concurrent.ThreadLocalRandom;
import java.util.concurrent.TimeUnit;

/**
 * 뉴스와 관련한 유틸 기능 클래스
 * @author kcw
 * @since 2026-01-07
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class NewsCrawlingUtils {

    private static final List<String> USER_AGENTS = List.of(
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36",
            "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/122.0.0.0 Safari/537.36",
            "Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36"
    );


    private static final List<String> REFERRERS = List.of(
            "https://www.google.com/",
            "https://search.naver.com/",
            "https://www.daum.net/"
    );

    private static final String ACCEPT_HEADER = "text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8";
    private static final String ACCEPT_LANGUAGE = "ko-KR,ko;q=0.9,en-US;q=0.8,en;q=0.7";

    // HTML 검색 요청 상수
    private static final Random RANDOM = new Random();
    private static final String USER_AGENT_CONTENT = "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/123.0.0.0 Safari/537.36";
    private static final String URL_GOOGLE = "https://www.google.com";
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final int THRESHOLD_LENGTH = 200;
    private static final long BASE_DELAY_MS = 2000;

    // 각 사이트마다 사용하는 selector
    private static final String SELECTORS_NAVER_NEWS = "#dic_area, .newsct_article, .go_trans._article_content";
    private static final String SELECTORS_NAVER_NEWS_MOBILE = "#dic_area, .media_end_body, #newsct_article, #content, .newsct_article";
    private static final String SELECTORS_ARTICLE = "article";
    private static final String SELECTORS_GOODNEWS = "#article-view-content-div";
    private static final String SELECTORS_ASIATODAY = ".news_bm, #font";
    private static final String SELECTORS_ETODAY = ".articleView";
    private static final String SELECTORS_NEWS2DAY = ".view_con_wrap:nth-child(2) body";
    private static final String SELECTORS_ETC =
            "#articleContent, .newsview_content, #article .content, #content, #news_body, #articleBody, .article-body, .article-content, .detail_editor, " +
            ".art_txt, .content-wrapper, .news_article, .read_txt, .view_cont, .nodeContentTitle, .news_body, .content-area, #article-view-content-div";

    private static final List<String> SELECTORS_DESCRIPTION = List.of(
            SELECTORS_NAVER_NEWS, SELECTORS_NAVER_NEWS_MOBILE, SELECTORS_ARTICLE, SELECTORS_GOODNEWS,
            SELECTORS_ASIATODAY, SELECTORS_ETODAY, SELECTORS_NEWS2DAY, SELECTORS_ETC
    );

    // 이미지 추출 selector
    private static final String ALT = "alt";
    private static final String CONTENT = "content";
    private static final String SELECTOR_IMAGE = "meta[property=og:image]";

    // 작성자 추출 selector
    private static final String SELECTOR_IMAGE_SITE = "meta[property=og:site_name]";
    private static final String SELECTOR_IMAGE_LOGO = ".media_end_head_top_logo_img";
    private static final String SELECTOR_TEXT_LOGO = ".press_logo";
    private static final String SELECTOR_TEXT_LOGO_IMG = ".press_logo img";
    private static final String SELECTOR_H1_IMG = "h1 img";
    private static final String SELECTOR_HEADER_IMG = "header img";
    private static final String DEFAULT_PUBLISHER = "네이버뉴스";


    /**
     * 뉴스 정보를 일괄 크롤링 및 추출
     * @param newsUrl 뉴스 기사 URL
     * @return 크롤링하여 추출한 뉴스 정보 DTO
     */
    public static NewsDto.CrawlResult extractAll(String newsUrl) {

        try {
            // [1] Chrome 브라우저 연결
            Document doc = connectToUrl(newsUrl);

            // [2] 기사 정보 일괄 추출
            String description = extractDescription(doc);
            String thumbnail = extractThumbnail(doc);
            String publisher = extractPublisher(doc);

            // [3] 유효한 결과인지 검증 (만약 본문, 썸네일, 뉴스언론사 정보 중 하나라도 없으면 실패)
            if (!(StrUtils.isValid(description) &&  StrUtils.isValid(thumbnail) && StrUtils.isValid(publisher)))
                throw new UtilsException(AppStatus.NEWS_CRAWL_EMPTY);

            // [4] 추출한 정보를 DTO에 담아 반환
            return NewsDto.CrawlResult.success(description, thumbnail, publisher);

        } catch (Exception e) {
            LogUtils.showWarn(NewsCrawlingUtils.class, "크롤링 실패! 추출 시도 기사 URL : %s, 예외 메세지 : %s", newsUrl, e.getMessage());
            return NewsDto.CrawlResult.fail();
        }
    }


    // 뉴스 URL 기반 크롤 환경으로 접속
    private static Document connectToUrl(String url) throws IOException, InterruptedException {

        // 랜덤한 userAgent, referrer 선택
        String userAgent = USER_AGENTS.get(RANDOM.nextInt(USER_AGENTS.size()));
        String referrer = REFERRERS.get(RANDOM.nextInt(REFERRERS.size()));
        //log.warn("userAgent = {}, referrer = {}", userAgent, referrer);

        // 임시 대기
        long jitter = ThreadLocalRandom.current().nextLong(-BASE_DELAY_MS / 2, BASE_DELAY_MS / 2 + 1);
        TimeUnit.MILLISECONDS.sleep(BASE_DELAY_MS + jitter);

        // 크롤링 수행 및 반환
        return Jsoup.connect(url)
                .userAgent(userAgent)
                .referrer(referrer)
                .header("Accept", ACCEPT_HEADER)
                .header("Accept-Language", ACCEPT_LANGUAGE)
                .header("Accept-Encoding", "gzip, deflate")
                .header("Upgrade-Insecure-Requests", "1")
                .header("Sec-Fetch-Dest", "document")
                .header("Sec-Fetch-Mode", "navigate")
                .header("Sec-Fetch-Site", "cross-site")
                .timeout((int) TIMEOUT.toMillis())
                .ignoreHttpErrors(true)
                .get();
    }


    // Document에서 본문 추출
    private static String extractDescription(Document doc) {

        // 네이버 뉴스 -> 네이버 모바일 뉴스 -> 언론사 홈페이지 -> 그 외 사이트 순으로 크롤링 수행
        // 정상 추출에 성공한 경우 반환 (50줄 이상의 내용 추출에 성공)
        for (String selector : SELECTORS_DESCRIPTION) {

            // selector 추출
            Element element = doc.selectFirst(selector);

            // 만약 크롤링 결과가 존재하면 추출 후 반환
            if (Objects.nonNull(element)){
                String description = cleanText(extractTextWithLineBreaks(element));
                if (description.length() >= THRESHOLD_LENGTH) return description;
            }
        }

        // 셀렉터에서 아무런 값도 얻지 못한 경우 빈 값
        return "";
    }


    // 썸네일 이미지 추출
    private static String extractThumbnail(Document doc) {
        return doc.select(SELECTOR_IMAGE).attr(CONTENT);
    }


    // 뉴스 퍼블리셔 (언론사 정보) 정보 추출
    private static String extractPublisher(Document doc) {

        // og:site_name 사용
        String press = doc.select(SELECTOR_IMAGE_SITE).attr(CONTENT);
        if (!press.isBlank()) return press;

        // 이미지 로고 alt
        press = doc.select(SELECTOR_IMAGE_LOGO).attr(ALT);
        if (!press.isBlank()) return press;

        // press_logo 내부 이미지 alt
        press = doc.select(SELECTOR_TEXT_LOGO_IMG).attr(ALT);
        if (!press.isBlank()) return press;

        // h1 내부 이미지 alt
        press = doc.select(SELECTOR_H1_IMG).attr(ALT);
        if (!press.isBlank()) return press;

        // header 내부 이미지 alt
        press = doc.select(SELECTOR_HEADER_IMG).attr(ALT);
        if (!press.isBlank()) return press;

        // 텍스트 로고
        press = doc.select(SELECTOR_TEXT_LOGO).text();
        if (!press.isBlank()) return press;

        // 모두 추출 실패 시, 기본 작성자 반환
        return DEFAULT_PUBLISHER;
    }


    // 줄바꿈을 보존하고 HTML 태그 제거
    private static String extractTextWithLineBreaks(Element element) {

        // [1] 불필요한 요소 제거
        element.select("a").remove();
        element.select(".relation_lst").remove();
        element.select(".end_photo_org").remove();
        element.select(".byline").remove();
        element.select(".journalist").remove();
        element.select(".reporter").remove();
        element.select("em.link_news").remove();

        // 표 관련 태그 제거
        element.select("table").remove(); // 표는 처음부터 하위요소 일괄 제거

        // [2] 줄바꿈 삽입
        element.select("br").before("\\n");
        element.select("p").before("\\n\\n");

        // [2] Jsoup.clean으로 HTML 태그 제거하면서 줄바꿈 보존
        String text = Jsoup.clean(
                element.html(),
                "",
                Safelist.none(),
                new Document.OutputSettings().prettyPrint(false)
        );

        // [3] 이스케이프된 줄바꿈을 실제 줄바꿈으로 변환
        return text.replace("\\n", "\n")
                .replaceAll("\\n{3,}", "\n\n")
                .trim();
    }


    // 뉴스에 불필요한 텍스트 제거
    private static String cleanText(String text) {

        if (Objects.isNull(text)) return "";

        return text
                // 1. 먼저 불필요한 텍스트 제거
                .replaceAll("\\[.*?기자\\]", "")
                .replaceAll("\\[.*?PD\\]", "")
                .replaceAll("\\[.*?앵커\\]", "")
                .replaceAll("▶.*", "")
                .replaceAll("(?m)^\\s*[ⓒⒸ©].*$", "")
                .replaceAll("무단.*?전재.*", "")
                .replaceAll("저작권자.*", "")
                .replaceAll("Copyright.*", "")
                .replaceAll("출처\\s*[=:].*", "")
                .replaceAll("관련기사", "")
                .replaceAll("#\\S+", "")
                .replaceAll("포스트\\s*태그.*", "")
                .replaceAll("글\\.\\s*[가-힣]+", "")

                // 2. 기자 정보 제거
                .replaceAll("(?m)^.*?기자\\s*$", "")
                .replaceAll("(?m)^\\[.*?기자.*?\\]\\s*$", "")
                .replaceAll("/\\s*[가-힣]{2,4}\\s*기자\\s*$", "")
                .replaceAll(".*@\\S+\\.(com|network).*", "")
                .replaceAll("(?m)^원문.*", "")
                .replaceAll("좋아요\\d+", "")  // 줄 시작 조건 제거
                .replaceAll("나빠요\\d+", "")  // 줄 시작 조건 제거

                // 3. HTML 엔티티 잔여물 제거
                .replaceAll("&[a-z]+;", "")  // &nbsp;, &lt;, &gt; 등 (변환 전)
                .replace("\u00A0", " ")      // non-breaking space를 일반 공백으로
                .replaceAll("<[^>]+>", "")

                // 4. 줄바꿈 정리
                .replaceAll("[ \\t\u00A0]+", " ")  // 공백, 탭, nbsp를 하나로
                .replaceAll("\\n{3,}", "\n\n")
                .replaceAll("(?m)^\\s+$", "")
                .trim();
    }



}
