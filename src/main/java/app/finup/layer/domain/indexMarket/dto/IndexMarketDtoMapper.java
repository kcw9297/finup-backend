package app.finup.layer.domain.indexMarket.dto;

import app.finup.layer.domain.indexMarket.entity.IndexMarket;
import lombok.*;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndexMarketDtoMapper {
    // 외부 API DTO(ApiRow) → Entity 변환
    public static IndexMarket toEntity(IndexMarketDto.ApiRow apiRow) {

        double closePrice = 0.0;
        double diff = 0.0;
        double rate = 0.0;

        if (apiRow.getClpr() != null) {
            closePrice = Double.parseDouble(apiRow.getClpr().replace(",", ""));
        }

        if (apiRow.getVs() != null) {
            diff = Double.parseDouble(apiRow.getVs());
        }

        if (apiRow.getFltRt() != null) {
            rate = Double.parseDouble(apiRow.getFltRt());
        }

        return IndexMarket.builder()
                .indexName(apiRow.getIdxNm())
                .closePrice(closePrice)
                .diff(diff)
                .rate(rate)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Entity → 내부 API 응답 DTO(Row) 변환
    public static IndexMarketDto.Row toRow(IndexMarket entity) {

        return IndexMarketDto.Row.builder()
                .idxNm(entity.getIndexName())
                .today(entity.getClosePrice())
                .rate(entity.getRate())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}