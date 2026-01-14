package app.finup.api.external.indicator.dto;

import app.finup.common.deserializer.CommaRemovingBigDecimalDeserializer;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.*;

import java.math.BigDecimal;

/**
 * 화
 * @author kcw
 * @since 2025-12-11
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndicatorApiDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class ExchangeRateRp {

        @JsonProperty("result")
        private Integer result; // 조회 결과 (1: 성공, 2: data코드오류, 3: 인증코드 오류, 4: 일일제한마감(최대 1000번))

        @JsonProperty("cur_unit")
        private String currencyCode; // 통화코드 (AUD, USD 등)

        @JsonProperty("cur_nm")
        private String currencyName; // 국가/통화명 (호주 달러, 스위스 프랑 등)

        @JsonProperty("ttb")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal buyingRate; // 전신환 매입률 (고객이 외화를 받을 때. 은행이 외화 매입)

        @JsonProperty("tts")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal sellingRate; // 전신환 매도률 (고객이 외화를 보낼 때. 은행이 외화 매도)

        @JsonProperty("deal_bas_r")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal baseRate; // 매매 기준율 (기준 환율. ttb와 tts의 중간 값)

        @JsonProperty("bkpr")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal bookPrice; // 장부가격 (회계상 장부에 기록되는 가격)

        @JsonProperty("yy_efee_r")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal yearlyCommissionRate; // 1년 환가료율 (수수료)

        @JsonProperty("ten_dd_efee_r")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal tenDayCommissionRate; // 10일환가료율 (수수료)

        @JsonProperty("kftc_bkpr")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal kftcBookPrice; // 서울외환중개 장부가격

        @JsonProperty("kftc_deal_bas_r")
        @JsonDeserialize(using = CommaRemovingBigDecimalDeserializer.class)
        private BigDecimal kftcBaseRate; // 서울외환중개 매매기준율
    }



    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ExchangeRateRow {

        private String currencyCode; // cur_unit
        private String currencyName; // cur_nm
        private BigDecimal baseRate; // deal_bas_r
        private BigDecimal buyingRate; // ttb
        private BigDecimal sellingRate; // tts
    }
}