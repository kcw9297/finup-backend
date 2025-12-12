package app.finup.layer.domain.exchangeRate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 홈페이지 환율 DTO 클래스
 * @author phj
 * @since 2025-12-11
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ExchangeRateDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private String curUnit;     // USD
        private String curNm;       // 미국 달러
        private String dealBasR;    // 1469.5
        private String updatedAt;   // 서버 기준 시간
    }

    // KEXIM API 전용 DTO
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiRow {
        @JsonProperty("cur_unit")
        private String curUnit;

        @JsonProperty("cur_nm")
        private String curNm;

        @JsonProperty("deal_bas_r")
        private String dealBasR;

        @JsonProperty("ttb")
        private String ttb;

        @JsonProperty("tts")
        private String tts;
    }
}