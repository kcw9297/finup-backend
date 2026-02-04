package app.finup.api.external.stock.dto;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.Duration;
import java.util.List;

/**
 * Stocks api 데이터 -> DTO 매퍼 클래스
 * @author kcw
 * @since 2025-12-28
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockApiDtoMapper {

    public static StockApiDto.Issue toIssue(StockApiDto.IssueRp rp) {

        return StockApiDto.Issue.builder()
                .accessToken(rp.getAccessToken())
                .ttl(Duration.ofSeconds(rp.getExpiresIn()))
                .build();
    }


    public static List<StockApiDto.MarketCapRow> toMarketCapRows(StockApiDto.MarketCapListRp rp) {

        return rp.getRows()
                .stream()
                .map(dto -> StockApiDto.MarketCapRow.builder()
                        .stockCode(dto.getStockCode())
                        .rank(dto.getRank())
                        .stockName(dto.getStockName())
                        .currentPrice(dto.getCurrentPrice())
                        .priceChange(dto.getPriceChange())
                        .priceChangeSign(dto.getPriceChangeSign())
                        .changeRate(dto.getChangeRate())
                        .marketCap(dto.getMarketCap())
                        .marketCapRatio(dto.getMarketCapRatio())
                        .build()
                )
                .toList();
    }

    public static List<StockApiDto.TradingValueRow> toTradingValueRows(StockApiDto.TradingValueListRp rp) {

        return rp.getRows()
                .stream()
                .map(dto -> StockApiDto.TradingValueRow.builder()
                        .stockName(dto.getStockName())
                        .stockCode(dto.getStockCode())
                        .rank(dto.getRank())
                        .currentPrice(dto.getCurrentPrice())
                        .priceChangeSign(dto.getPriceChangeSign())
                        .priceChange(dto.getPriceChange())
                        .changeRate(dto.getChangeRate())
                        .accumulatedTradingValue(dto.getAccumulatedTradingValue())
                        .averageVolume(dto.getAverageVolume())
                        .accumulatedVolume(dto.getAccumulatedVolume())
                        .build()
                )
                .toList();
    }

    public static StockApiDto.Detail toDetail(StockApiDto.DetailRp rp) {

        // [1] rp 내 상세데이터 추출
        StockApiDto.DetailRp.Detail dt = rp.getDetail();

        // [2] DTO 변환
        return StockApiDto.Detail.builder()
                .stockCode(dt.getStockCode())
                .currentPrice(dt.getCurrentPrice())
                .marketIndexName(dt.getMarketIndexName())
                .sectorName(dt.getSectorName())
                .faceValue(dt.getFaceValue())
                .marketCap(dt.getMarketCap())
                .listedShares(dt.getListedShares())
                .week52High(dt.getWeek52High())
                .week52Low(dt.getWeek52Low())
                .days250High(dt.getDays250High())
                .days250Low(dt.getDays250Low())
                .per(dt.getPer())
                .pbr(dt.getPbr())
                .eps(dt.getEps())
                .bps(dt.getBps())
                .foreignNetBuyQty(dt.getForeignNetBuyQty())
                .programNetBuyQty(dt.getProgramNetBuyQty())
                .foreignOwnershipRate(dt.getForeignOwnershipRate())
                .volumeTurnoverRate(dt.getVolumeTurnoverRate())
                .tempStopYn(dt.getTempStopYn())
                .investmentCautionYn(dt.getInvestmentCautionYn())
                .shortOverYn(dt.getShortOverYn())
                .managementIssueCode(dt.getManagementIssueCode())
                .build();
    }



    public static List<StockApiDto.Candle> toCandles(StockApiDto.CandleRp rp) {

        return rp.getCandles()
                .stream()
                .map(dto -> StockApiDto.Candle.builder()
                        .tradingDate(dto.getTradingDate())
                        .openPrice(dto.getOpenPrice())
                        .highPrice(dto.getHighPrice())
                        .lowPrice(dto.getLowPrice())
                        .closePrice(dto.getClosePrice())
                        .accumulatedVolume(dto.getAccumulatedVolume())
                        .build()
                )
                .toList();
    }



}
