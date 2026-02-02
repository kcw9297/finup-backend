package app.finup.api.external.marketindex.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class MarketIndexApiDtoMapper {

    public static List<MarketIndexApiDto.Row> toRows(MarketIndexApiDto.IndexListRp rp) {

        // [1] 필요 데이터 추출
        MarketIndexApiDto.IndexListRp.Response response = rp.getResponse();
        MarketIndexApiDto.IndexListRp.Body body = response.getBody();
        MarketIndexApiDto.IndexListRp.Items items = body.getItems();

        // [2] 결과 반환
        return items.getItem()
                .stream()
                .map(item -> MarketIndexApiDto.Row.builder()
                        .indexName(item.getIndexName())
                        .closingPrice(item.getClosingPrice())
                        .changeFromPrevious(item.getChangeFromPrevious())
                        .fluctuationRate(item.getFluctuationRate())
                        .build())
                .toList();
    }

}