package app.finup.infra.ai.provider;


import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.infra.ai.enums.ChatOption;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.chat.model.ChatResponse;
import org.springframework.ai.chat.prompt.Prompt;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.ai.openai.OpenAiChatOptions;
import org.springframework.stereotype.Component;

import java.util.Map;
import java.util.Objects;

/**
 * OpenAI 기반 질문을 전달하는 ChatProvider 구현체
 * @author kcw
 * @since 2025-12-16
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAiChatProvider implements ChatProvider {

    // OPEN AI Model 의존성
    private final OpenAiChatModel openAiChatModel;

    // 옵션 상수
    private static final Map<ChatOption, OpenAiChatOptions> OPTIONS_MAP = Map.of(

            // 정확성 최우선 - 규칙 엄수, 일관된 결과
            ChatOption.STRICT, OpenAiChatOptions.builder()
                    .temperature(0.0) // 결정적, 일관된 응답
                    .build(),

            // 제한된 다양성 - 데이터 기반 해석
            ChatOption.MODERATE, OpenAiChatOptions.builder()
                    .temperature(0.3)         // 적당히 다양성을 가지는 응답
                    .presencePenalty(0.2)     // 제한된 관점
                    .frequencyPenalty(0.2)    // 제한
                    .build(),

            // 높은 다양성 - 다양한 관점/표현
            ChatOption.CREATIVE, OpenAiChatOptions.builder()
                    .temperature(1.0)       // 다양성을 가지는 응답 (1.0이 사실상 최대치)
                    .presencePenalty(0.6)   // 새로운 주제/관점 유도
                    .frequencyPenalty(0.4)  // 반복 표현 억제
                    .build()
    );


    @Override
    public String sendQuery(String prompt, ChatOption chatOption) {

        // [1] 질문 응답
        ChatResponse response = openAiChatModel.call(new Prompt(prompt, OPTIONS_MAP.get(chatOption)));

        // [2] 만약 질문 결과가 null인 경우 예외 반환
        String result = response.getResult().getOutput().getText();

        if (Objects.isNull(result) || result.isBlank())
            throw new ProviderException(AppStatus.AI_CHAT_RESPONSE_ERROR);

        return result;
    }

}
