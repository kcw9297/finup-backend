package app.finup.layer.domain.quiz.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Quiz AI 데이터 -> DTO 매퍼 클래스
 * @author lky
 * @since 2025-12-16
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class QuizDtoMapper {

    public static List<QuizDto.Question> toQuestion(Map<String,Object> map) {

        // [1] output 꺼내기
        Object output = map.get("output");
        if(!(output instanceof List<?> outputList)) {
            throw new IllegalArgumentException("AI 응답에 output 배열이 없습니다.");
        }
        List<QuizDto.Question> questionList = new ArrayList<>();

        // [2] 문제 하나씩 매핑
        for (Object item : outputList) {

            if (!(item instanceof Map<?, ?> itemMap)) {
                continue; // 비정상 데이터 스킵
            }

            Map<String, Object> questionAi = (Map<String, Object>) itemMap;

            String question = (String) questionAi.get("question");
            List<String> choices = (List<String>) questionAi.get("choices");
            Integer answer = (Integer) questionAi.get("answer");
            String explanation = (String) questionAi.get("explanation");

            questionList.add(
                    QuizDto.Question.builder()
                            .question(question)
                            .choices(choices)
                            .answer(answer)
                            .explanation(explanation)
                            .build()
            );
        }

        return questionList;
    }
}
