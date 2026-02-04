package app.finup.common.utils;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.util.Arrays;
import java.util.Collection;
import java.util.Objects;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class AiUtils {

    /**
     * Embedded 요청에 필요한 문자열 조합
     * @param params 조합 요청 객체 (복수개 가능)
     * @return 조합 문자열
     */
    public static String generateEmbeddingText(Object... params) {

        return Arrays.stream(params)
                .filter(Objects::nonNull) // null 제거
                .map(obj -> // 컬렉션이 있는 경우, 문자열로 별도 변환
                    obj instanceof Collection ?
                            ((Collection<?>) obj).stream().map(String::valueOf).collect(Collectors.joining(" ")) :
                            String.valueOf(obj) // 일반적인 객체면 즉시 문자열로 변환
                )
                .collect(Collectors.joining(" ")) // 띄어쓰기 기준으로 문자열 합침
                .trim(); // 양끝 공백 제거
    }


    /**
     * embedded float 배열을 byte 문자열로 변환 (효율적 저장 및 조회 가능)
     * @param embedded 임베딩된 float 배열
     * @return byte 배열로 변환된 임베딩 배열
     */
    public static byte[] convertToByteArray(float[] embedded) {

        // [1] buffer 할당
        ByteBuffer buffer = ByteBuffer.allocate(embedded.length * 4);
        buffer.order(ByteOrder.LITTLE_ENDIAN);

        // [2] 변환 수행
        for (float value : embedded) buffer.putFloat(value);
        return buffer.array();
    }


    /**
     * AI 프롬포트 결과에 포함된 불필요한 텍스트 제거 (마크다운 블록 등)
     * @param text AI 생성결과 문자열
     * @return 불필요한 텍스트를 제거한 문자열
     */
    public static String removeMarkdown(String text) {

        if (Objects.isNull(text) || text.isBlank()) {
            return "";
        }

        return text
                // [1] Markdown 코드블록 제거 (```언어\n내용\n``` 형태)
                .replaceAll("```[a-z]*\\n?", "")
                .replaceAll("```", "")

                // [2] 단일 백틱 제거 (`내용` 형태)
                .replaceAll("^`|`$", "")

                // [3] JSON 따옴표 제거 (전체가 "내용" 형태)
                .replaceAll("^\"", "")
                .replaceAll("\"$", "")

                // [4] 불필요한 접두어 제거
                .replaceAll("^(분석|결과|답변|응답|해설)\\s*:\\s*", "")

                // [5] 앞뒤 공백 제거
                .trim();
    }
}
