package app.finup.layer.domain.exchangeRate.dto;

import app.finup.layer.domain.exchangeRate.entity.ExchangeRate;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExchangeRateDtoMapper {

    // 신규 통화 최초 저장용
    public static ExchangeRate toNewEntity(ExchangeRateDto.ApiRow apiRow, LocalDate rateDate) {
        double rate = parse(apiRow.getDealBasR());
        return ExchangeRate.builder()
                .currency(apiRow.getCurUnit())
                .todayRate(rate)
                .yesterdayRate(rate)
                .rateDate(rateDate)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Entity → 홈 응답 DTO 변환
    public static ExchangeRateDto.Row toRow(ExchangeRate entity) {
        return ExchangeRateDto.Row.builder()
                .curUnit(entity.getCurrency())
                .curNm(getCurrencyName(entity.getCurrency()))
                .today(entity.getTodayRate())
                .yesterday(entity.getYesterdayRate())
                .updatedAt(entity.getUpdatedAt().toString())
                .build();
    }

    private static double parse(String rate) {
        return Double.parseDouble(rate.replace(",", ""));
    }

    private static String getCurrencyName(String unit) {
        return switch (unit) {
            case "USD" -> "미국 달러";
            case "JPY", "JPY(100)" -> "일본 엔";
            default -> unit;
        };
    }
}
