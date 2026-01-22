package app.finup.layer.domain.stock.service;

import app.finup.layer.domain.stock.dto.StockDto;
import java.util.List;

/**
 * 주식 핵심 기능 제공 인터페이스
 * @author kcw
 * @since 2025-12-25
 */
public interface StockService {

    /**
     * 주식 정보 제공을 위한 토큰 발급
     * @return 발급받은 API AccessToken 문자열
     */
    String issueToken();


    /**
     * 주식 파일 초기화
     */
    void initStockFile();


    /**
     * 시가 총액 순 주식목록 조회 (상위 30개)
     * @return 시가총액 순 상위 30개 주식 종목 DTO 목록
     */
    List<StockDto.MarketCapRow> getMarketCapList();


    /**
     * 거래량 순 주식목록 조회 (상위 30개)
     * @return 거래량 순 상위 30개 주식 종목 DTO 목록
     */
    List<StockDto.TradingValueRow> getTradingValueList();


    /**
     * 특정 주식의 차트 정보 조회
     * @param stockCode 대상 주식 코드
     * @return 30일분 일/주/월봉 차트 정보 DTO
     */
    StockDto.Chart getChart(String stockCode);


    /**
     * 특정 주식 상세정보 조회
     * @param stockCode 대상 주식 코드
     * @return 주식 상세 정보 DTO
     */
    StockDto.Detail getDetail(String stockCode);

}
