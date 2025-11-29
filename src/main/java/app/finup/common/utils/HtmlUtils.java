package app.finup.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;

import java.util.List;
import java.util.Objects;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class HtmlUtils {

    // HTML Safelist (불필요한 태그가 삽입되지 않도록, 아래 태그와 속성만 허용)
    private static final Safelist SAFELIST = Safelist.relaxed()
            .addTags("font", "mark", "video", "source", "iframe")
            // img src 허용
            .addAttributes("img", "src", "style", "width", "height", "class")
            .addAttributes("span", "style", "class")
            .addAttributes("font", "face", "size", "color", "style")
            .addAttributes("p", "style", "class", "align")
            .addAttributes("div", "style", "class", "align")
            .addAttributes("h1", "style", "class")
            .addAttributes("h2", "style", "class")
            .addAttributes("h3", "style", "class")
            .addAttributes("h4", "style", "class")
            .addAttributes("h5", "style", "class")
            .addAttributes("h6", "style", "class")
            .addAttributes("blockquote", "style", "class")
            .addAttributes("pre", "style", "class")
            .addAttributes("code", "style", "class")
            .addAttributes("strong", "style", "class")
            .addAttributes("em", "style", "class")
            .addAttributes("u", "style", "class")
            .addAttributes("a", "href", "target", "rel") // href 추가
            .addAttributes("table", "style", "class", "border", "width", "cellpadding", "cellspacing")
            .addAttributes("tr", "style", "class")
            .addAttributes("td", "style", "class", "colspan", "rowspan", "width", "height")
            .addAttributes("th", "style", "class", "colspan", "rowspan", "width", "height", "scope")
            .addAttributes("thead", "style", "class")
            .addAttributes("tbody", "style", "class")
            .addAttributes("ul", "style", "class")
            .addAttributes("ol", "style", "class", "start", "type")
            .addAttributes("li", "style", "class")
            .addAttributes("video", "src", "controls", "width", "height", "style", "class")
            .addAttributes("source", "src", "type")
            .addAttributes("iframe", "src", "width", "height", "style", "class", "frameborder", "allowfullscreen")
            .preserveRelativeLinks(true);


    /**
     * 불필요한 HTML 태그 정화(제거)
     * @param htmlContent 원문 HTML 문자열
     * @return 정화된 HTML 문자열
     */
    public static String purifyHtml(String htmlContent) {

        return Objects.isNull(htmlContent) || htmlContent.isBlank() ?
                "" :
                Jsoup.clean(
                        htmlContent,
                        "https://example.com", // 상대경로 허용을 위한 더미 주소
                        SAFELIST, // custom safelist
                        new Document.OutputSettings().prettyPrint(false) // 개행문자 허용
                );
    }

    /**
     * HTML 정화 후, 내부 이미지 src 요소 추출
     * @param htmlContent 원문 HTML 문자열
     * @return 정화 후 추출된 이미지 파일명 리스트
     */
    public static List<String> purifyAndExtractImageNameFromHtml(String htmlContent) {

        // [1] HTML 내 위험 코드 정화
        String purified = purifyHtml(htmlContent);
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
     * @param htmlContent 원문 HTML 문자열
     * @return 사용자가 작성한 "순수" 문자열 반환
     */
    public static String removeHtmlTags(String htmlContent) {
        return Objects.isNull(htmlContent) ? null : Jsoup.parse(htmlContent).text();
    }
}


