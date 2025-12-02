package app.finup.layer.domain.news.util;

import java.util.List;

public final class KeywordUtils {

    public static List<String> expand(String keyword) {
        return List.of(
                keyword,
                keyword + " 전망",
                keyword + " 분석",
                keyword + " 실적",
                keyword + " 주가",
                keyword + " 투자"
        );
    }
}
