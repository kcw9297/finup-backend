package app.finup.infra.ai.provider;


import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.embedding.EmbeddingModel;
import org.springframework.ai.embedding.EmbeddingResponse;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.Map;

/**
 * OpenAI(ChatGPT) 기반 임베딩을 제공하는 EmbeddingProvider 구현체
 * @author kcw
 * @since 2025-12-16
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class OpenAIEmbeddingProvider implements EmbeddingProvider {

    // embedding에 사용할 model 의존성
    private final EmbeddingModel embeddingModel;

    @Override
    public float[] generate(String text) {

        // [1] embedding 수행
        EmbeddingResponse response = embeddingModel.embedForResponse(List.of(text));

        // [2] Spring AI의 Embedding 객체 내 float[] 문자열 추출 (임베딩 배열)
        //response.getResult().get(0)

        return new float[0];
    }

    @Override
    public float[] generate(Map<Long, String> texts) {
        return new float[0];
    }
}
