package app.finup.layer.domain.indicator.dto;

import lombok.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;

/**
 * 홈페이지 경지 지표 DTO 클래스
 * @author kcw
 * @since 2026-01-14
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class IndicatorDto {


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class FinancialIndexRow {

        private String indexName; // 지표 이름 (JPY/KRW, 코스피, ...)
        private BigDecimal todayValue; // 오늘 지수 값
        private BigDecimal todayFluctuationRate; // 이전 거래일 대비 변화율. 오늘 변동률 (%)
        private LocalDate updatedAt;  // 서버 기준 시간
    }


    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class MarketIndexRow {

        private String indexName; // 지표 이름 (JPY/KRW, 코스피, ...)
        private BigDecimal todayValue; // 오늘 지수 값
        private BigDecimal todayFluctuationRate; // 이전 거래일 대비 변화율. 오늘 변동률 (%)
        private LocalDate updatedAt;  // 서버 기준 시간
    }


}