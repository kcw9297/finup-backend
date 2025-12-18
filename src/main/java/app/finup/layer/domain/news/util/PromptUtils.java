package app.finup.layer.domain.news.util;


public class PromptUtils {
    private static final int MAX_CHARS = 6000;

    public static String trimForAi(String text){
        if(text == null) return "";
        if(text.length() <= MAX_CHARS) return text;
        return text.substring(0, MAX_CHARS);
    }

    public static String trimHeadTail(String text) {
        if (text == null || text.isBlank()) return "";
        int max = 6000;
        if (text.length() <= max) return text;

        int half = max / 2;
        return text.substring(0, half)
                + "\n...\n"
                + text.substring(text.length() - half);
    }
}
