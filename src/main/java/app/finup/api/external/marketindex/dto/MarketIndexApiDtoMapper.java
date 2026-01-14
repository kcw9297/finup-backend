package app.finup.api.external.marketindex.dto;

import app.finup.layer.domain.indexMarket.entity.IndexMarket;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarketIndexApiDtoMapper {
    // 외부 API DTO(ApiRow) → Entity 변환
    public static IndexMarket toEntity(MarketIndexApiDto.Row marketIdexRow) {

        double closePrice = 0.0;
        double diff = 0.0;
        double rate = 0.0;

        if (marketIdexRow.getClpr() != null) {
            try {
                closePrice = Double.parseDouble(marketIdexRow.getClpr().replace(",", ""));
            } catch (NumberFormatException e) {
                closePrice = 0.0;
            }
        }
        if (marketIdexRow.getVs() != null) {
            diff = Double.parseDouble(marketIdexRow.getVs());
        }
        if (marketIdexRow.getFltRt() != null) {
            rate = Double.parseDouble(marketIdexRow.getFltRt());
        }

        return IndexMarket.builder()
                .indexName(marketIdexRow.getIdxNm())
                .closePrice(closePrice)
                .diff(diff)
                .rate(rate)
                .updatedAt(LocalDateTime.now())
                .build();
    }

    // Entity → 내부 API 응답 DTO(Row) 변환
    public static MarketIndexApiDto.Row toRow(IndexMarket entity) {

        return MarketIndexApiDto.Row.builder()
                .idxNm(entity.getIndexName())
                .today(entity.getClosePrice())
                .rate(entity.getRate())
                .updatedAt(entity.getUpdatedAt())
                .build();
    }
}