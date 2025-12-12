package app.finup.layer.domain.exchangeRate.dto;

import app.finup.layer.domain.exchangeRate.entity.ExchangeRate;
import com.fasterxml.jackson.databind.JsonNode;
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
        return ExchangeRateDto.Row.builder()
                .curUnit(entity.getCurrency())
                .dealBasR(String.valueOf(entity.getDealBasR()))
                .updatedAt(entity.getUpdatedAt().toString())
                .build();
    }

    // 외부 API DTO → 응답 DTO
    public static ExchangeRateDto.Row fromApi(ExchangeRateDto.ApiRow apiRow) {
        return ExchangeRateDto.Row.builder()
                .curUnit(apiRow.getCurUnit())
                .curNm(apiRow.getCurNm())
                .dealBasR(apiRow.getDealBasR())
                .updatedAt(LocalDateTime.now().toString())
                .build();
    }
}
