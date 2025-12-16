package app.finup.infra.ai.provider;

import java.util.Map;

/**
 * Vector 유사도 검색을 위한 Embedding 을 제공하는 기능 인터페이스
 * @author kcw
 * @since 2025-12-16
 */
public interface EmbeddingProvider {

    /**
     * 제공 단일 텍스트 기반 Embedding 배열 생성
     * @param text 객체 정보 기반으로 생성된 문자열 (ex. 단어명 + 단어뜻 으로 생성된 문자열)
     * @return 생성된 임베딩 배열 (byte 변환까지 처리)
     */
    byte[] generate(String text);


    /**
     * 제공 텍스트 기반 Embedding 배열 생성 (batch 처리. 최대 50개만 가능)
     * @param idTextMap "고유번호" - "텍스트" 쌍의 Map 요청 (다수 요청을 동시 처리)
     * @return "고유번호" - "임베딩 배열" 쌍 (각 번호마다 텍스트를 처리한 결과)
     */
    <T> Map<T, byte[]> generate(Map<T, String> idTextMap);



}
