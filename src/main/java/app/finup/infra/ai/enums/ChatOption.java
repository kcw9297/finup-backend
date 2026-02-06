package app.finup.infra.ai.enums;

/**
 * AI 요청 옵션 값을 관리하는 열거형 상수 클래스
 * @author kcw
 * @since 2026-02-06
 */
public enum ChatOption {
    STRICT, // 제한적 (뉴스 단어 분석, RAG, 컨텐츠 추천 등)
    MODERATE, // 제한된 다양성 (차트 분석 등)
    CREATIVE  // 높은 다양성 (뉴스 분석 등)
}
