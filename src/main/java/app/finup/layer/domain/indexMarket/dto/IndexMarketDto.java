package app.finup.layer.domain.indexMarket.dto;

import com.fasterxml.jackson.annotation.*;
import lombok.*;

import java.time.LocalDateTime;

/**
 * 홈페이지 증시 DTO 클래스
 * @author phj
 * @since 2025-12-14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndexMarketDto {
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Row {
        private String idxNm;        // 지수명
        private double today;        // 오늘 종가
        private double rate;         // 이전 거래일 대비 변화율 (%)
        private LocalDateTime updatedAt;
    }

    // 공공데이터포털 API 전용 DTO
    @JsonIgnoreProperties(ignoreUnknown = true)
    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class ApiRow {

        @JsonProperty("idxNm")
        private String idxNm;

        @JsonProperty("clpr")
        private String clpr;

        @JsonProperty("vs")
        private String vs;

        @JsonProperty("fltRt")
        private String fltRt;
    }
}