package app.finup.layer.domain.financeDictionary.dto;

import app.finup.layer.domain.financeDictionary.entity.FinanceDictionary;
import lombok.*;

/**
 * Finance Dictionary Entity -> DTO 매퍼 클래스
 * @author khj
 * @since 2025-12-10
 */

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class FinanceDictionaryDtoMapper {
    public static FinanceDictionaryDto.Row toRow(FinanceDictionary entity) {
        return FinanceDictionaryDto.Row.builder()
                .termId(entity.getTermId())
                .name(entity.getName())
                .description(entity.getDescription())
                .build();
    }

    public static FinanceDictionary toEntity(FinanceDictionaryDto.Row row) {
        return FinanceDictionary.builder()
                .name(row.getName())
                .description(row.getDescription())
                .build();
    }
}
