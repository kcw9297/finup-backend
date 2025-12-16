package app.finup.infra.ai.provider;


import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.StrUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiChatModel;
import org.springframework.stereotype.Component;

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

    @Override
    public <T> T question(String prompt, Class<T> type) {

        // [1] 질문 응답
        String jsonAnswer = openAiChatModel.call(prompt);

        // [2] 만약 질문 결과가 null인 경우 예외 반환
        if (Objects.isNull(jsonAnswer) || jsonAnswer.isBlank())
            throw new ProviderException(AppStatus.AI_CHAT_RESPONSE_ERROR);

        // [3] 응답 메세지를 제공한 타입으로 변환 및 반환
        return StrUtils.fromJson(jsonAnswer, type);
    }
}
