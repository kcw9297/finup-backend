package app.finup.layer.domain.exchangeRate.dto;

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
        private String result;         // 조회 결과
        private String curUnit;        // 통화코드 USD, JPY
        private String curNm;          // 국가명
        private String ttb;            // 전신환(송금) 받으실 때
        private String tts;            // 전신환(송금) 보내실 때
        private String dealBasR;       // 매매 기준율
        private String updatedAt;   // 마지막 갱신 시간
    }
}