package app.finup.layer.domain.exchangeRate.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

import java.time.LocalDateTime;

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
        private String curUnit;     // 영어 이름
        private String curNm;       // 한국 이름
        private double today;       // 오늘 환율
        private double yesterday;  // 어제 환율
        private String updatedAt;   // 서버 기준 시간
    }

    // 한국수출입은행 API 전용 DTO
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