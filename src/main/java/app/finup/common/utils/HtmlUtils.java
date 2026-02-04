package app.finup.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.util.List;
import java.util.Objects;

/**
 * HTML 문자열 취급 유틸 클래스
 * @author kcw
 * @since 2025-12-24
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class HtmlUtils {

    // 줄바꿈을 허용하지 않는 OUTPUT
    private static final Document.OutputSettings NO_PRETTY_PRINT =
            new Document.OutputSettings().prettyPrint(false);


    /**
     * 불필요한 HTML 태그 정화(제거)
     * @param text 원문 문자열
     * @return 정화된 HTML 문자열
     */
    public static String purifyHtml(String text) {

        return Objects.isNull(text) || text.isBlank() ?
                "" :
                Jsoup.clean(
                        text,
                        "https://example.com", // 상대경로 허용을 위한 더미 주소
                        Safelist.none(), // custom safelist
                        new Document.OutputSettings().prettyPrint(false) // 개행문자 불허
                );
    }

    /**
     * HTML 정화 후, 내부 이미지 src 요소 추출
     * @param text 원문 HTML 문자열
     * @return 정화 후 추출된 이미지 파일명 리스트
     */
    public static List<String> purifyAndExtractImageNameFromHtmlText(String text) {

        // [1] HTML 내 위험 코드 정화
        String purified = purifyHtml(text);
        Document purifiedDoc = Jsoup.parse(purified);

        // [2] 이미지 태그만 추출 후  src 속성만 추출 후 반환
        return purifiedDoc.select("img").stream()
                .map(img -> img.attr("src"))
                .filter(src -> !src.isBlank())
                .map(src -> src.substring(src.lastIndexOf('/') + 1))
                .toList();
    }


    /**
     * HTML 태그를 제거한 문자열만 추출
     * @param text 원문 문자열
     * @return 사용자가 작성한 "순수" 문자열 반환
     */
    public static String getText(String text) {

        return Objects.isNull(text) ? "" :
                Jsoup.clean(text, "", Safelist.none(), NO_PRETTY_PRINT)
                .replace("&nbsp;", " ")
                .replace("&lt;", "<")
                .replace("&gt;", ">")
                .replace("&amp;", "&")
                .replace("&quot;", "\"")
                .replaceAll("\\s+", " ")
                .trim();
    }
}
