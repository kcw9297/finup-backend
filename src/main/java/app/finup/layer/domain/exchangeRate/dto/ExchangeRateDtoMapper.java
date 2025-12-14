package app.finup.layer.domain.exchangeRate.dto;

import app.finup.layer.domain.exchangeRate.entity.ExchangeRate;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class ExchangeRateDtoMapper {
    // 외부 API DTO(Row) → Entity 변환
    public static ExchangeRate toEntity(ExchangeRateDto.ApiRow apiRow) {
        double dealBasR = 0.0;
        if (apiRow.getDealBasR() != null) {
            dealBasR = Double.parseDouble(apiRow.getDealBasR().replace(",", ""));
        }
        return ExchangeRate.builder()
                .currency(apiRow.getCurUnit())
                .dealBasR(dealBasR)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Entity → 내부 API 응답 DTO(Row) 변환
    public static ExchangeRateDto.Row toRow(ExchangeRate entity) {
        double today = entity.getDealBasR();
        return ExchangeRateDto.Row.builder()
                .curUnit(entity.getCurrency())
                .curNm(null)
                .today(today)
                .yesterday(today)
                .updatedAt(entity.getUpdatedAt().toString())
                .build();
    }
}
