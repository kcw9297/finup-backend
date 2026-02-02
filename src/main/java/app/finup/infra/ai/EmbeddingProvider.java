package app.finup.infra.ai;

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
     * 제공 텍스트 기반 Embedding 배열 생성 (다수 처리)
     * @param idTextMap Map<고유번호, 임베딩요청 텍스트>
     * @return Map<고유번호, 임베딩배열>
     */
    <T> Map<T, byte[]> generate(Map<T, String> idTextMap);



}
