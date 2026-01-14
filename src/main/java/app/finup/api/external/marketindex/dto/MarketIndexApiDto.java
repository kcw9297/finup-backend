package app.finup.api.external.marketindex.dto;

import app.finup.common.deserializer.LocalDateDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;

/**
 * 시장 지수(인덱스) API 조회 결과를 담는 DTO 클래스
 * @author kcw
 * @since 2026-01-14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketIndexApiDto {

    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class IndexListRp {

        private Response response; // 응답 데이터가 들어있는 클래스


        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Response {
            private Header header;
            private Body body;
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Header {
            private String resultCode; // 응답 코드 ("00" 이 아닌 다른 값이 온 경우 오류)
            private String resultMsg;  // 응답 메세지
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Body {
            private int numOfRows; // 응답 row 개수
            private int pageNo; // 응답 데이터 페이지
            private int totalCount; // 총 데이터 수
            private Items items; // 응답 데이터 목록
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Items {
            private List<Item> item; // 응답 목록
        }

        @Getter
        @JsonIgnoreProperties(ignoreUnknown = true)
        public static class Item {

            @JsonProperty("basDt")
            @JsonDeserialize(using = LocalDateDeserializer.class)
            private LocalDate baseDate; // 기준일자 (YYYYMMDD 형식)

            @JsonProperty("idxNm")
            private String indexName; // 지수명

            @JsonProperty("idxCsf")
            private String indexClassification; // 지수분류

            @JsonProperty("epyItmsCnt")
            private Integer constituentStockCount; // 구성종목수

            @JsonProperty("clpr")
            private BigDecimal closingPrice; // 종가

            @JsonProperty("vs")
            private BigDecimal changeFromPrevious; // 전일대비

            @JsonProperty("fltRt")
            private BigDecimal fluctuationRate; // 등락률

            @JsonProperty("mkp")
            private BigDecimal openingPrice; // 시가

            @JsonProperty("hipr")
            private BigDecimal highPrice; // 고가

            @JsonProperty("lopr")
            private BigDecimal lowPrice; // 저가

            @JsonProperty("trqu")
            private Long tradingVolume; // 거래량

            @JsonProperty("trPrc")
            private Long tradingValue; // 거래대금

            @JsonProperty("lstgMrktTotAmt")
            private Long marketCapitalization; // 시가총액

            @JsonProperty("lsYrEdVsFltRg")
            private Integer yearEndChangeAmount; // 연말대비등락폭

            @JsonProperty("lsYrEdVsFltRt")
            private BigDecimal yearEndChangeRate; // 연말대비등락률

            @JsonProperty("yrWRcrdHgst")
            private BigDecimal yearHighPrice; // 연중최고

            @JsonProperty("yrWRcrdLwst")
            private BigDecimal yearLowPrice; // 연중최저

            @JsonProperty("yrWRcrdHgstDt")
            @JsonDeserialize(using = LocalDateDeserializer.class)
            private LocalDate yearHighDate; // 연중최고일자 (YYYYMMDD)

            @JsonProperty("yrWRcrdLwstDt")
            @JsonDeserialize(using = LocalDateDeserializer.class)
            private LocalDate yearLowDate; // 연중최저일자 (YYYYMMDD)

            @JsonProperty("basPntm")
            @JsonDeserialize(using = LocalDateDeserializer.class)
            private LocalDate basePointDate; // 기준시점 (YYYYMMDD)

            @JsonProperty("basIdx")
            private Integer baseIndexValue; // 기준지수
        }
    }


    // 사용자에게 반환하는 지수 클래스
    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Row {

        private String indexName; // idxNm
        private BigDecimal closingPrice; // clpr
        private BigDecimal changeFromPrevious; // changeFromPrevious
        private BigDecimal fluctuationRate; // fltRt
    }

}