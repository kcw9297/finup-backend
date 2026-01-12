package app.finup.infra.api.stock.provider;

import app.finup.infra.api.stock.dto.StockApiDto;
import app.finup.infra.api.stock.enums.CandleType;

import java.util.List;

/**
 * KIS API 사용을 위한 접큰 토큰 발급 인터페이스
 * @author kcw
 * @since 2025-12-25
 */
public interface StockProvider {

    /**
     * AT(AccessToken) 발급
     * @return 발급된 AccessToken 문자열을 포함한 발급 토큰정보 DTO
     */
    StockApiDto.Issue issueToken();


    /**
     * 시가총액순 주식 리스트 조회
     * @param accessToken API 요청에 필요한 AT
     * @return API 응답 시가총액순 주식 DTO 리스트
     */
    List<StockApiDto.MarketCapRow> getMarketCapRankList(String accessToken);


    /**
     * 거래량순 주식 리스트 조회
     * @param accessToken API 요청에 필요한 AT
     * @return API 응답 거래량순 주식 DTO 리스트
     */
    List<StockApiDto.TradingValueRow> getTradingValueList(String accessToken);


    /**
     * 주식 상세정보 조회
     * @param code 주식 고유코드
     * @param accessToken API 요청에 필요한 AT
     * @return API 응답 주식 상세정보 DTO
     */
    StockApiDto.Detail getDetail(String code, String accessToken);


    /**
     * 주식 상세 차트(캔들) 목록 정보 조회 (최근 30개의 캔들 조회)
     * @param code 주식 고유코드
     * @param accessToken API 요청에 필요한 AT
     * @param candleType 조회할 차트 유형 (일봉차트, 주봉차트, 월봉차트)
     * @return API 응답 주식 상세 차트(캔들) DTO 목록
     */
    List<StockApiDto.Candle> getCandleList(String code, String accessToken, CandleType candleType);
}
