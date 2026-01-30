package app.finup.layer.domain.news.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.AppException;
import app.finup.common.exception.UtilsException;
import app.finup.common.utils.HtmlUtils;
import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.news.dto.NewsDto;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Connection;
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

    // HTML 검색 요청 상수
    private static final Random RANDOM = new Random();
    private static final Duration TIMEOUT = Duration.ofSeconds(30);
    private static final int LENGTH_MIN_DESCRIPTION = 600;
    private static final long BASE_DELAY_MS = 500;

    // 각 사이트마다 사용하는 selector
    private static final String SELECTOR_META_DESCRIPTION = "meta[property=og:description]";
    private static final List<String> SELECTORS_DESCRIPTION = List.of(
            "#dic_area", ".newsct_article", ".go_trans._article_content", // 네이버뉴스
            ".media_end_body", "#newsct_article", // 네이버뉴스 모바일
            "article", // Article 태그
            "#article-view-content-div", // 굿뉴스
            ".news_bm", "#font", // 아시아투데이
            "div.articleView > p", // 이투데이
            ".view_con_wrap:nth-child(2) body", // 뉴스투데이
            "#news_body_area", ".smartOutput", // 프라임경제
            ".detailCont", // 글로벌이코노믹
            ".viewSection", "#article_main", // 더벨
            "div.text", // 대한경제
            ".view_con_wrap", // 뉴스투데이

            // 기타 언론사 (공통)
            "#articleContent", ".newsview_content", "#article .content", "#content",
            "#news_body", "#articleBody", ".article-body", ".article-content",
            ".detail_editor", ".art_txt", ".content-wrapper", ".news_article",
            "view_con_wrap", ".article-view", ".read_txt", ".view_cont", ".nodeContentTitle", ".news_body", ".content-area",
            ".article-txt-contents", ".article_content", ".news_content", ".article_txt_container",
            "#news_body_area_contents", "#newsView", ".edit-txt"
    );


    // 이미지 추출 selector
    private static final String ALT = "alt";
    private static final String CONTENT = "content";
    private static final String SELECTOR_META_IMAGE = "meta[property=og:image]";

    // 작성자 추출 selector
    private static final String SELECTOR_META_AUTHOR = "meta[name=Author]";
    private static final String SELECTOR_META_SITE_MANE = "meta[property=og:site_name]";
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

            // [3] 유효한 결과인지 검증 (본문 내용은 없는 경우 기사 제공의 의미가 없으므로 실패 처리)
            if (!StrUtils.isValid(description))
                throw new UtilsException(AppStatus.UTILS_CRAWL_RESPONSE_EMPTY);

            // [4] 추출한 정보를 DTO에 담아 반환
            return NewsDto.CrawlResult.success(description, thumbnail, publisher);

        } catch (AppException e) {
            if (e.getAppStatus().getHttpCode() == 429) throw e; // 다른 로직에서 재시도하도록 예외 던짐
            else return showLogAndRequestFail(newsUrl, e);

        } catch (Exception e) {
            return showLogAndRequestFail(newsUrl, e);
        }
    }


    // 요청 실패 시, 로그 출력 후 실패 객체 반환
    private static NewsDto.CrawlResult showLogAndRequestFail(String newsUrl, Exception e) {
        //LogUtils.showWarn(NewsCrawlingUtils.class, "크롤링 실패! 추출 시도 기사 URL : %s, 예외 메세지 : %s", newsUrl, e.getMessage());
        return NewsDto.CrawlResult.fail();
    }


    // 뉴스 URL 기반 크롤 환경으로 접속
    private static Document connectToUrl(String url) throws IOException, InterruptedException {

        // 랜덤한 userAgent, referrer 선택
        String userAgent = USER_AGENTS.get(RANDOM.nextInt(USER_AGENTS.size()));
        String referrer = REFERRERS.get(RANDOM.nextInt(REFERRERS.size()));
        //log.warn("userAgent = {}, referrer = {}", userAgent, referrer);

        // 대기
        long jitter = ThreadLocalRandom.current().nextLong(-BASE_DELAY_MS / 2, BASE_DELAY_MS / 2 + 1);
        TimeUnit.MILLISECONDS.sleep(BASE_DELAY_MS + jitter);

        // 크롤링 수행 및 반환
        Connection.Response response = Jsoup.connect(url)
                .userAgent(userAgent)
                .referrer(referrer)
                .header("Accept", "text/html")
                .header("Accept-Language", "ko-KR,ko;q=0.9")
                .timeout((int) TIMEOUT.toMillis())
                .ignoreHttpErrors(true)
                .execute();

        int status = response.statusCode();
        //log.warn("[NEWS-CRAWL] status={}, length={}", status, response.body().length());

        // status 값이 4xx, 5xx와 같은 오류를 반환한 경우
        // 만약 429 (Too Many Request) 인 경우, 추후 재시도를 유도하도록 다른 상태 반환
        if (status >= 400) {
            if (status == 429) throw new UtilsException(AppStatus.TOO_MANY_REQUEST);
            else throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED);
        }

        // 그 외 정상 반환인 경우
        return response.parse();
    }

    // 본문 추출
    private static String extractDescription(Document doc) {

        for (String selector : SELECTORS_DESCRIPTION) {
            for (Element element : doc.select(selector)) {
                Element el = element.clone();
                String description = cleanText(extractTextWithLineBreaks(el));
                if (description.length() >= LENGTH_MIN_DESCRIPTION) return HtmlUtils.getText(description);
            }
        }

        // 아무 내용도 찾지 못하거나, 최소 본문길이를 만족하지 못한 경우 meta 데이터에서 추출 시도
        String description = cleanText(doc.select(SELECTOR_META_DESCRIPTION).attr(CONTENT));
        return description.length() >= LENGTH_MIN_DESCRIPTION ? HtmlUtils.getText(description) : "";
    }


    // 썸네일 이미지 추출
    private static String extractThumbnail(Document doc) {
        return doc.select(SELECTOR_META_IMAGE).attr(CONTENT);
    }


    // 뉴스 퍼블리셔 (언론사 정보) 정보 추출
    private static String extractPublisher(Document doc) {

        // [1] 추출 수행
        String press = doc.select(SELECTOR_META_AUTHOR).attr(CONTENT);
        if (!press.isBlank()) return HtmlUtils.getText(press);

        press = doc.select(SELECTOR_IMAGE_LOGO).attr(ALT);
        if (!press.isBlank()) return HtmlUtils.getText(press);

        press = doc.select(SELECTOR_TEXT_LOGO_IMG).attr(ALT);
        if (!press.isBlank()) return HtmlUtils.getText(press);

        //press = doc.select(SELECTOR_H1_IMG).attr(ALT);
        //if (!press.isBlank()) return HtmlUtils.getText(press);

        press = doc.select(SELECTOR_HEADER_IMG).attr(ALT);
        if (!press.isBlank()) return HtmlUtils.getText(press);

        press = doc.select(SELECTOR_TEXT_LOGO).text();
        if (!press.isBlank()) return HtmlUtils.getText(press);

        press = doc.select(SELECTOR_META_SITE_MANE).attr(CONTENT);
        if (!press.isBlank()) return HtmlUtils.getText(press);

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
        element.select(".relation_newslist").remove();
        element.select(".kwd_tags").remove();
        element.select(".card-container").remove();
        element.select(".reporter_topNews").remove();
        element.select(".recommend_btn").remove();
        element.select(".ico_share").remove();
        element.select(".maj_list_wrap").remove();
        element.select(".writer").remove();
        element.select(".view-article").remove();
        element.select(".article-copy").remove();
        element.select(".social-group").remove();
        element.select(".footer-menu").remove();
        element.select(".user-address").remove();
        element.select(".auto-article").remove();
        element.select("figcaption").remove();
        element.select("center").remove();
        element.select("h6").remove();
        element.select("div[style*='border:1px']").remove();
        element.select(".copyright").remove();
        element.select(".media_end_categorize").remove();
        element.select(".newsct_journalist").remove();
        element.select("#channelBanner").remove();
        element.select(".media_end_linked").remove();
        element.select(".promotion").remove();
        element.select(".ends_btn").remove();
        element.select(".subscribe_cta_layer").remove();
        element.select(".media_journalistcard").remove();

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
