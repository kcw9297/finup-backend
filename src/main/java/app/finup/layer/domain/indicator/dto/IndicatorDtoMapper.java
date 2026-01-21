package app.finup.layer.domain.indicator.dto;

import app.finup.api.external.financialindex.dto.FinancialIndexApiDto;
import app.finup.api.external.marketindex.dto.MarketIndexApiDto;
import app.finup.common.utils.TimeUtils;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * 지표 정보를 담기 위한 DTO 클래스
 * @author kcw
 * @since 2026-01-15
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndicatorDtoMapper {

    public static List<IndicatorDto.MarketIndexRow> toMarketIndexRows(List<MarketIndexApiDto.Row> rows) {

        return rows.stream()
                .map(row -> IndicatorDto.MarketIndexRow.builder()
                        .indexName(row.getIndexName())
                        .todayValue(row.getClosingPrice())
                        .todayFluctuationRate(row.getFluctuationRate())
                        .updatedAt(TimeUtils.getNowLocalDate())
                        .build())
                .toList();

    }

    public static List<IndicatorDto.FinancialIndexRow> toFinancialIndexRow(
            List<FinancialIndexApiDto.ExchangeRateRow> prevRows,
            List<FinancialIndexApiDto.ExchangeRateRow> todayRows) {

        // [1] 등락률 계산을 위해 Map 생성
        Map<String, FinancialIndexApiDto.ExchangeRateRow> prevRowMap =
                prevRows.stream()
                        .collect(Collectors.toMap(
                                FinancialIndexApiDto.ExchangeRateRow::getCurrencyName,
                                Function.identity()
                        ));

        // [2] 오늘 영업일 지표 정보 매핑
        return todayRows.stream()
                .filter(Objects::nonNull)
                .map(todayRow -> {

                    // [1] 현재 지표와 대응하는 지난일 지표 조회
                    FinancialIndexApiDto.ExchangeRateRow prevRow = prevRowMap.get(todayRow.getCurrencyName());
                    if (Objects.isNull(prevRow)) return null; // 비교 대상이 없는 경우 null

                    // [2] 등락률 & 등락 값 계산
                    BigDecimal yesterdayRate = prevRow.getBaseRate(); // 어제환율
                    BigDecimal todayRate = todayRow.getBaseRate(); // 오늘환율

                    // 계산 수행 (BigDecimal 메소드 사용)
                    BigDecimal fluctuationValue = todayRate.subtract(yesterdayRate); // 오늘 환율 - 어제 환율
                    BigDecimal fluctuationRate = fluctuationValue
                            .divide(yesterdayRate, 4, RoundingMode.HALF_UP) // 어제 환율로 나눈 후, 소숫점 4번째 자리까지 출력 (이후 자리 반올림)
                            .multiply(new BigDecimal("100")); // 그 이후 100 곱함


                    // [2] 지표명 뒤에 /KRW 추가 (만약 "(100)" 문자열이 있는 경우 제거)
                    String indexName = "%s/KRW".formatted(todayRow.getCurrencyCode().replace("(100)", ""));

                    return IndicatorDto.FinancialIndexRow.builder()
                            .indexName(indexName)
                            .todayValue(fluctuationValue)
                            .todayFluctuationRate(fluctuationRate)
                            .updatedAt(TimeUtils.getNowLocalDate())
                            .build();

                })
                .filter(Objects::nonNull) // 계산에 실패한 지표 제거
                .toList();
    }
}
