package app.finup.layer.domain.news.util;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.springframework.stereotype.Component;

@Component
public class NewsScraper {
    public String extractThumbnail(String link) {
        try {
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .get();

            String og = doc.select("meta[property=og:image]").attr("content");
            return (og == null || og.isBlank())
                    ? "/default-news.png"
                    : og;
        } catch (Exception e) {
            return "/default-news.png";
        }
    }

    public String extractPublisher(String link){
        try{
            Document doc = Jsoup.connect(link)
                    .userAgent("Mozilla/5.0")
                    .get();
            // 1) og:site_name 사용
            String press = doc.select("meta[property=og:site_name]").attr("content");
            if (!press.isBlank()) return press;

            // 2) 이미지 로고 alt
            press = doc.select(".media_end_head_top_logo_img").attr("alt");
            if (!press.isBlank()) return press;

            // 3) 텍스트 로고
            press = doc.select(".press_logo").text();
            if (!press.isBlank()) return press;

        }catch (Exception e){

        }
        return "네이버뉴스";
    }
}
