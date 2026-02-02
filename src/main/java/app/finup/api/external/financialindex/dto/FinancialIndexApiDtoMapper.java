package app.finup.api.external.financialindex.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class FinancialIndexApiDtoMapper {


    public static List<FinancialIndexApiDto.ExchangeRateRow> toExchangeRateRows(
            List<FinancialIndexApiDto.ExchangeRateRp> rp) {

        return rp.stream()
                .map(exchangeRateRp -> FinancialIndexApiDto.ExchangeRateRow.builder()
                        .currencyCode(exchangeRateRp.getCurrencyCode())
                        .currencyName(exchangeRateRp.getCurrencyName())
                        .baseRate(exchangeRateRp.getBaseRate())
                        .buyingRate(exchangeRateRp.getBuyingRate())
                        .sellingRate(exchangeRateRp.getSellingRate())
                        .build()
                ).toList();
    }

}
