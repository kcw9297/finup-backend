package app.finup.infra.ai;

/**
 * AI 모델에게 질문을 전달하는 기능을 제공하는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */
public interface ChatProvider {

    /**
     * AI 모델에게 프롬포트 전달 (질문)
     * @param prompt AI에게 전달할 질문 프롬프트 문자열
     * @return AI에게 얻은 답변 문자열 (JSON 변환 필요 시 다른 메소드 사용)
     */
    String query(String prompt);


    /**
     * AI 모델에게 프롬포트 전달 (질문)
     * @param prompt AI에게 전달할 질문 프롬프트 문자열
     *               (반드시 프롬프트 내 응답할 JSON 구조와 JSON 문자열로만 응답할 것을 명시)
     * @param type JSON으로 응답받은 데이터를 변환(역직렬화)할 데이터 타입
     * @return AI에게 얻은 답변 기반 변환된 DTO 데이터
     */
    <T> T queryJson(String prompt, Class<T> type);

}
