package app.finup.api.external.indicator.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;


@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class IndicatorApiDtoMapper {


    public static List<IndicatorApiDto.ExchangeRateRow> toExchangeRateRows(
            List<IndicatorApiDto.ExchangeRateRp> rp) {

        return rp.stream()
                .map(exchangeRateRp -> IndicatorApiDto.ExchangeRateRow.builder()
                        .currencyCode(exchangeRateRp.getCurrencyCode())
                        .currencyName(exchangeRateRp.getCurrencyName())
                        .baseRate(exchangeRateRp.getBaseRate())
                        .buyingRate(exchangeRateRp.getBuyingRate())
                        .sellingRate(exchangeRateRp.getSellingRate())
                        .build()
                ).toList();
    }

}
