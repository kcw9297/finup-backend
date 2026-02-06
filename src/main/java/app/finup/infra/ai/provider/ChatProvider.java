package app.finup.infra.ai.provider;

import app.finup.infra.ai.enums.ChatOption;

/**
 * AI 모델에게 질문을 전달하는 기능을 제공하는 인터페이스
 * @author kcw
 * @since 2025-12-16
 */
public interface ChatProvider {

    /**
     * AI 모델에게 분석을 위한 프롬포트 전달
     * @param prompt     AI에게 전달할 질문 프롬프트 문자열
     * @param chatOption AI Chat 옵션 상수 값 (어떤 성격의 AI가 필요한지)
     * @return AI에게 얻은 답변 문자열 (JSON 변환 필요 시 다른 메소드 사용)
     */
    String sendQuery(String prompt, ChatOption chatOption);

}
