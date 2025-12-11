package app.finup.infra.dictionary.provider;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.text.StringEscapeUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class HtmlCleaner {
    public static String clean(String html) {
        if (html == null) return "";

        // HTML 엔티티(&lt; &gt; &nbsp; 등) 해제
        String unescaped = StringEscapeUtils.unescapeHtml4(html);
        
        // HTML 태그 제거 -> 순수 텍스트만 추출
        Document doc = Jsoup.parse(unescaped);
        return doc.text();
    }
    public static String escapeSql(String text) {
        if (text == null) return "";
        return text.replace("'", "''");
    }
}
