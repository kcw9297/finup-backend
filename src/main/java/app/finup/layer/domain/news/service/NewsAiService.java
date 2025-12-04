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
        당신은 '초보자도 이해할 수 있게 뉴스를 설명하는' 금융 전문 AI 분석가입니다.
        기사 전체를 읽고 아래 3가지 항목을 JSON으로만 출력하세요.
        
        ### 출력 형식
        {
          "summary": "...",
          "keywords": [
            { "term": "...", "definition": "..." },
            { "term": "...", "definition": "..." },
            { "term": "...", "definition": "..." }
          ],
          "insight": "..."
        }
        
        ### 지시사항
        
        1) summary
        - 기사 핵심 내용을 5-8줄로 요약
        - 내용은 쉽고 부드럽게, 경제 초보자도 이해 가능하도록 작성
        - 불필요한 기업명/인명/날짜는 최소화
        
        2) keywords
        - '경제·투자 개념·시장 구조' 중심의 개념적 키워드 5개와 뜻풀이 한문장
        - 기업명/기관명/인명/브랜드명/지명 절대 포함하지 말 것
        - 예시: 금리 인상, 물가 상승률, 재무 구조, 시장 변동성, 기술주, 유동성, 수요 둔화, 공급망, 인플레이션 등
        - 키워드들은 모두 개념형 단어여야 함
        - 반드시 다음 형식을 사용할 것:
          { "term": "용어", "definition": "한 문장 뜻풀이" }
        
        3) insight (해설 + 분석 통합)
        - 초보자 기준으로 쉽게 풀어서 설명
        - 해당 뉴스가 의미하는 경제적 맥락 + 시장/산업에 미칠 수 있는 영향까지
        - 지나친 투자 조언, 매수/매도 표현 금지
        - 한 문단(5~7줄)로 작성
        
        ### 규칙
        - 반드시 JSON만 출력
        - JSON 밖 텍스트 금지
        - 문자열 내 줄바꿈 최소화
        - key 이름(term, definition)은 절대 변경 금지
        
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
            // AI가 JSON을 깔끔하게 못 생성한 경우 대비
            return Map.of(
                    "summary", "AI 분석에 실패했습니다.",
                    "keywords", List.of(),
                    "insight", "다시 시도해주세요."
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
