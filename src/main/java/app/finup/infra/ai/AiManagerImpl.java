package app.finup.infra.ai;

import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AiManagerImpl implements AiManager {
    private  final OpenAiChatModel openAiChatModel;
    private final ObjectMapper objectMapper;

    public Map<String, Object> runJsonPrompt(String prompt) {

        ChatResponse response = openAiChatModel.call(new Prompt(prompt));
        String output = extractJson(response.getResult().getOutput().getText());
        try {
            return objectMapper.readValue(output, Map.class);
        } catch (Exception e){
            return Map.of(
                    "error", true,
                    "message", "AI 분석 실패"
            );
        }
    }
    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text;
    }
}
