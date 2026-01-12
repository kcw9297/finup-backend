package app.finup.layer.domain.news.service;


/**
 * 뉴스 AI 분석 기능 제공 인터페이스
 * @author kcw
 * @since 2025-12-25
 */

public interface NewsAiService {

    /**
     * AI 뉴스 분석
     * @param newsId 분석 대상 뉴스번호
     * @param memberId 분석 요청 회원번호
     * @return 뉴스 AI 분석 결과 DTO
     */
    String analyze(Long newsId, Long memberId);

}
