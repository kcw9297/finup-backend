package app.finup.layer.domain.stock.dto;

import lombok.*;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 주식 종목 DTO 클래스
 * @author kcw
 * @since 2025-12-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockDto {


    /**
     * 종목과 관련한 정보를 모두 보관하기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Info implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private MarketCapRow marketCap; // 시가총액 순 조회 시 표시 데이터
        private TradingValueRow tradingValue; // 거래대금 순 조회 시 표시 데이터
        private Detail detail; // 종목 상세 데이터
        private Chart chart; // 일/주/월봉 차트 데이터
    }



    /**
     * 종목 시가총액 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketCapRow implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String stockCode; // 유가증권 단축 종목코드
        private Integer rank; // 데이터 순위
        private String stockName; // HTS 한글 종목명
        private Long currentPrice; // 주식 현재가
        private Long priceChange; // 전일 대비
        private Integer priceChangeSign; // 전일 대비 부호
        private Double changeRate; // 전일 대비율
        private Long marketCap; // 시가 총액
        private Double marketCapRatio; // 시장 전체 시가총액 비중
    }

    /**
     * 종목 거래대금 순위 리스트 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class TradingValueRow implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        private String stockName; // HTS 한글 종목명
        private String stockCode; // 유가증권 단축 종목코드
        private Integer rank; // 데이터 순위
        private Long currentPrice; // 주식 현재가
        private Integer priceChangeSign; // 전일 대비 부호
        private Long priceChange; // 전일 대비
        private Double changeRate;// 전일 대비율
        private Long accumulatedTradingValue; // 누적 거래 대금
        private Long averageVolume; // 평균 거래량
        private Long accumulatedVolume; // 누적 거래량

    }

    /**
     * 종목 상세페이지 종목 정보 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Detail implements Serializable {

        @Serial
        private static final long serialVersionUID = 1L;

        //종목 기본 정보
        //[1] 종목명 헤더
        private String stockName; // 한글 종목명
        private String stockCode; // 주식 단축 종목코드 // 005930
        private Long currentPrice; // 주식 현재가 // 104900
        private String marketIndexName; // 대표 시장 한글 명 //KOSPI200

        //[2] 종목 카드
        private String sectorName; // 업종 한글 종목명 // 전기·전자
        private Integer faceValue; // 주식 액면가 // 100
        private Long marketCap; // HTS 시가총액 // 6209700
        private Long listedShares; // 상장 주수 // 5919637922

        //투자지표
        //[1] 가격
        private Long week52High; // 52주일 최고가 // 112400
        private Long week52Low; // 52주일 최저가 // 50800
        private Long days250High; // 250일 최고가 // 112400
        private Long days250Low; // 250일 최저가 // 50800

        //[2] 가치평가
        private Double per; // PER // 21.19
        private Double pbr; // PBR // 1.81
        private Double eps; // EPS // 4950.00
        private Double bps; // BPS / /57930.00

        //[3] 수급 거래
        private Long foreignNetBuyQty; // 외국인 순매수 수량 // 0
        private Long programNetBuyQty; // 프로그램매매 순매수 수량 // -48796
        private Double foreignOwnershipRate; // HTS 외국인 소진율 // 52.22
        private Double volumeTurnoverRate; // 거래량 회전율 // 0.20

        //[4] 리스크 상태
        private Boolean tempStop; // 임시 정지 여부  // N
        private Boolean investmentCaution; // 투자유의여부 // N
        private Boolean shortOver; // 단기과열여부 // N
        private Boolean managementIssueCode; // 관리종목여부 // N

    }


    /**
     * 일/주/월봉 정보를 가진 차트 정보를 담을 DTO
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Chart {

        private List<Candle> dayCandles; // 일봉 30일치
        private List<Candle> weekCandles; // 주봉 30주치
        private List<Candle> monthCandles; // 월봉 30개월 치
    }


    /**
     * 차트(캔들)정보 조회 결과를 담기 위해 사용
     */
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Candle {

        private String tradingDate; // 거래일자
        private Long openPrice; // 시가
        private Long highPrice; // 고가
        private Long lowPrice; // 저가
        private Long closePrice; // 종가
        private Long accumulatedVolume;  // 누적 거래량

        // 직접 계산
        private Double ma5; // 5일 이동평균
        private Double ma20; // 20일 이동평균
        private Double ma60; // 60일 이동평균
        private Double volumeMa5; // 거래량 5일 평균
        private Double volumeMa20; // 거래량 20일 평균
    }


}