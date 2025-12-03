package app.finup.layer.domain.news.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
@RequiredArgsConstructor
public class NewsAiService {
    private final OpenAiChatModel openAiChatModel;
    private final ObjectMapper mapper = new ObjectMapper();
    public Map<String, Object> analyzeArticle(String article) throws JsonProcessingException {

        String prompt = """
        당신은 초보 투자자에게 뉴스를 쉽게 설명하는 AI 분석가입니다.
        아래 기사의 전문을 읽고 다음 4가지를 JSON 형식으로 출력하세요.

        1) summary : 기사 핵심 내용을 초보자도 이해할 수 있게 4~6줄로 요약
        2) keywords : 경제·주식·시장 관련 핵심 키워드 5개 (어려운 전문 용어 금지)
        3) explanation : 초보자용 쉬운 해설 (뉴스가 의미하는 점을 한 문단으로)
        4) analysis : 시장/산업/기업에 미칠 영향에 대한 중립적 분석

        반드시 JSON만 출력하세요.

        기사 전문:
        """ + article;
        ChatResponse response = openAiChatModel.call(
                new Prompt(prompt)
        );

        String aiOutput = response.getResult().getOutput().getText();

        aiOutput = extractJson(aiOutput);

        try {
            return mapper.readValue(aiOutput, Map.class);
        } catch (Exception e) {
            // 🟥 AI가 JSON을 깔끔하게 못 생성한 경우 대비
            return Map.of(
                    "summary", "AI 분석에 실패했습니다.",
                    "keywords", List.of(),
                    "explanation", "본문이 너무 짧거나 형식이 올바르지 않을 수 있습니다.",
                    "analysis", "다시 시도해주세요."
            );
        }
    }

    private String extractJson(String text) {
        int start = text.indexOf("{");
        int end = text.lastIndexOf("}");
        if (start != -1 && end != -1 && end > start) {
            return text.substring(start, end + 1);
        }
        return text; // JSON이 제대로 형성된 경우
    }
}
