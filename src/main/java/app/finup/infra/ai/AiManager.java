package app.finup.infra.ai;

import java.util.Map;

public interface AiManager {
    Map<String, Object> runJsonPrompt(String prompt);
    float[] embed (String text);
}
