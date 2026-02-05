package app.finup.layer.domain.news.service;


import app.finup.layer.domain.news.dto.NewsAiDto;

import java.util.List;

/**
 * 뉴스 AI 분석 기능 제공 인터페이스
 * @author kcw
 * @since 2025-12-25
 */

public interface NewsAiService {

    /**
     * AI 뉴스 분석 조회
     * @param newsId 분석 대상 뉴스번호
     * @param memberId 분석 요청 회원번호
     * @return 뉴스 AI 분석 결과 문자열
     */
    String getAnalysis(Long newsId, Long memberId);


    /**
     * AI 뉴스 재 분석 및 조회(기존 캐시를 무효하고 다시 분석)
     * @param newsId 분석 대상 뉴스번호
     * @param memberId 분석 요청 회원번호
     * @return 뉴스 AI 분석 결과 문자열
     */
    String retryAndGetAnalyze(Long newsId, Long memberId);


    /**
     * AI 뉴스 분석 조회
     *
     * @param newsId   분석 대상 뉴스번호
     * @param memberId 분석 요청 회원번호
     * @return 뉴스 AI 분석 결과 DTO
     */
    List<NewsAiDto.AnalysisWords> getAnalysisWords(Long newsId, Long memberId);


    /**
     * AI 뉴스 분석 조회
     *
     * @param newsId   분석 대상 뉴스번호
     * @param memberId 분석 요청 회원번호
     * @return 뉴스 AI 분석 결과 DTO
     */
    List<NewsAiDto.AnalysisWords> retryAndGetAnalysisWords(Long newsId, Long memberId);
}
