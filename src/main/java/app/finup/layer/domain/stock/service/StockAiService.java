package app.finup.layer.domain.stock.service;


import app.finup.layer.domain.stock.dto.StockAiDto;
import app.finup.layer.domain.stock.enums.ChartType;

import java.util.List;

/**
 * 주식 AI 분석 기능 제공 인터페이스
 * @author kcw
 * @since 2026-01-05
 */
public interface StockAiService {

    /**
     * AI 차트 분석
     * @param stockCode 대상 주식 코드
     * @param memberId 분석 요청 회원번호
     * @param chartType 차트 타입 (일봉차트, 주봉차트, 월봉차트)
     * @return AI 차트 분석 결과 정보 DTO
     */
    StockAiDto.ChartAnalyzation analyzeChart(String stockCode, Long memberId, ChartType chartType);


    /**
     * AI 차트 재분석 (캐시 정보 무효하고 다시 분석)
     * @param stockCode 대상 주식 코드
     * @param memberId 분석 요청 회원번호
     * @param chartType 차트 타입 (일봉차트, 주봉차트, 월봉차트)
     * @return AI 차트 분석 결과 정보 DTO
     */
    StockAiDto.ChartAnalyzation retryAnalyzeChart(String stockCode, Long memberId, ChartType chartType);


    /**
     * AI 종목상세 분석
     * @param stockCode 대상 주식 코드
     * @param memberId 분석 요청 회원번호
     * @return 종목 상세 분석 결과 DTO
     */
    StockAiDto.DetailAnalyzation analyzeDetail(String stockCode, Long memberId);


    /**
     * AI 종목상세 재분석 (캐시 정보 무효하고 다시 분석)
     * @param stockCode 대상 주식 코드
     * @param memberId 분석 요청 회원번호
     * @return 종목 상세 분석 결과 DTO
     */
    StockAiDto.DetailAnalyzation retryAnalyzeDetail(String stockCode, Long memberId);


    /**
     * AI 유튜브 영상 추천
     * @param stockCode 대상 주식 코드
     * @param memberId 분석 요청 회원번호
     * @return 추천된 종목 유튜브 영상 정보 DTO 목록
     */
    List<StockAiDto.YouTubeRecommendation> recommendYouTube(String stockCode, Long memberId);


    /**
     * AI 유튜브 영상 재추천 (캐시 정보 무효하고 다시 추천)
     * @param stockCode 대상 주식 코드
     * @param memberId 분석 요청 회원번호
     * @return 추천된 종목 유튜브 영상 정보 DTO 목록
     */
    List<StockAiDto.YouTubeRecommendation> retryRecommendYouTube(String stockCode, Long memberId);

}
