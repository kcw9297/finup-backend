package app.finup.infra.ai;


import app.finup.common.utils.AiUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.ai.openai.OpenAiEmbeddingModel;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

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
    private final OpenAiEmbeddingModel embeddingModel;


    @Override
    public byte[] generate(String text) {
        return AiUtils.convertToByteArray(embeddingModel.embed(text));
    }


    @Override
    public <T> Map<T, byte[]> generate(Map<T, String> idTextMap) {

        // [1] 만약 빈 map 제공 시, API 요청 미수행
        if (Objects.isNull(idTextMap) || idTextMap.isEmpty()) return Map.of();

        // [2] id(PK), text 분리
        List<T> ids = new ArrayList<>(idTextMap.keySet());
        List<String> texts = ids.stream().map(idTextMap::get).toList(); // ids 순서대로 리스트가 생성되도록 보장

        // [3] 임베딩 수행
        List<float[]> results = embeddingModel.embed(texts);

        // [4] 임베딩 결과를 Map에 매핑하여 반환
        return IntStream.range(0, results.size())
                .boxed()
                .collect(Collectors.toConcurrentMap(
                        ids::get,
                        i -> AiUtils.convertToByteArray(results.get(i))
                ));
    }
}
