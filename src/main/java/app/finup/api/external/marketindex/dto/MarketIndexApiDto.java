package app.finup.api.external.marketindex.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 시장 지수(인덱스) API 조회 결과를 담는 DTO 클래스
 * @author kcw
 * @since 2026-01-14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class MarketIndexApiDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class DetailRp {

        @JsonProperty("idxNm")
        private String idxNm;

        @JsonProperty("clpr")
        private String clpr;

        @JsonProperty("vs")
        private String vs;

        @JsonProperty("fltRt")
        private String fltRt;
    }


    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

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