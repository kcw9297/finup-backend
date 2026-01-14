package app.finup.layer.domain.economicindicator.dto;

import lombok.*;

/**
 * 홈페이지 환율 DTO 클래스
 * @author kcw
 * @since 2026-01-14
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

}