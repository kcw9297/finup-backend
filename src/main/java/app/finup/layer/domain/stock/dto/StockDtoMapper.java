package app.finup.layer.domain.stock.dto;

import app.finup.infra.api.stock.dto.StockApiDto;
import app.finup.infra.api.youtube.dto.YouTube;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;


/**
 * Stocks api 데이터 -> DTO 매퍼 클래스
 * @author kcw
 * @since 2025-12-29
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class StockDtoMapper {

    // 날짜 포메팅
    private static final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyyMMdd");

    public static StockDto.Chart toChart(
            List<StockApiDto.Candle> dayCandles,
            List<StockApiDto.Candle> weekCandles,
            List<StockApiDto.Candle> monthCandles) {

        return StockDto.Chart.builder()
                .dayCandles(toCandle(dayCandles))
                .weekCandles(toCandle(weekCandles))
                .monthCandles(toCandle(monthCandles))
                .build();
    }


    // API Candle DTO -> 사용자에거 보여줄 캔들 DTO 변롼
    private static List<StockDto.Candle> toCandle(List<StockApiDto.Candle> candles) {

        // [1] 정렬 후 매핑 수행
        List<StockDto.Candle> result = candles.stream()
                .sorted(Comparator.comparing(candle -> LocalDate.parse(candle.getTradingDate(), formatter)))
                .map(candle -> StockDto.Candle.builder()
                        .tradingDate(candle.getTradingDate())
                        .openPrice(candle.getOpenPrice())
                        .highPrice(candle.getHighPrice())
                        .lowPrice(candle.getLowPrice())
                        .closePrice(candle.getClosePrice())
                        .accumulatedVolume(candle.getAccumulatedVolume())
                        .build()
                )
                .toList();

        // [2] 이동평균 계산 후 결과에 삽입
        calculateMovingAverage(result, 5, false);   // MA5
        calculateMovingAverage(result, 20, false);  // MA20
        calculateMovingAverage(result, 60, false);  // MA60

        // 거래량 이동평균
        calculateMovingAverage(result, 5, true);    // Volume MA5
        calculateMovingAverage(result, 20, true);   // Volume MA20

        // [3] 결과 반환
        return result;
    }


    // 이동평균 계산
    private static void calculateMovingAverage(List<StockDto.Candle> candles,
                                               int period, boolean isVolume) {

        // [1] 계산하기 충분한 데이터가 존재하는지 확인
        if (Objects.isNull(candles) || candles.size() < period) return;

        // [2] 이동평균 계산
        for (int i = period - 1; i < candles.size(); i++) {
            double sum = 0.0;

            // 이전 period개 데이터의 합계
            for (int j = i - period + 1; j <= i; j++) {
                StockDto.Candle candle = candles.get(j);
                sum += isVolume
                        ? (double) candle.getAccumulatedVolume()
                        : (double) candle.getClosePrice();
            }

            // 이동평균 설정
            double ma = sum / period;
            setMovingAverageValue(candles.get(i), ma, period, isVolume);
        }
    }

    // 계산한 결과 삽입
    private static void setMovingAverageValue(
            StockDto.Candle candle,
            double ma,
            int period,
            boolean isVolume) {

        if (isVolume) {
            switch (period) {
                case 5 -> candle.setVolumeMa5(ma);
                case 20 -> candle.setVolumeMa20(ma);
            }
        } else {
            switch (period) {
                case 5 -> candle.setMa5(ma);
                case 20 -> candle.setMa20(ma);
                case 60 -> candle.setMa60(ma);
            }
        }
    }


    public static StockDto.Info toInfo(
            StockDto.Chart chart,
            StockApiDto.Detail detail,
            StockApiDto.MarketCapRow marketCapRow,
            StockApiDto.TradingValueRow tradingValueRow) {

        return StockDto.Info.builder()
                .marketCap(Objects.isNull(marketCapRow) ? null : toMarketCapRow(marketCapRow))
                .tradingValue(Objects.isNull(tradingValueRow) ? null : toTradingValueRow(tradingValueRow))
                .detail(toDetail(detail, getStockName(marketCapRow, tradingValueRow)))
                .chart(chart)
                .build();
    }


    // 시가총액 순으로 검색한 API 결과 DTO 변환
    private static StockDto.MarketCapRow toMarketCapRow(StockApiDto.MarketCapRow row) {

        return StockDto.MarketCapRow.builder()
                .stockCode(row.getStockCode())
                .rank(row.getRank())
                .stockName(row.getStockName())
                .currentPrice(row.getCurrentPrice())
                .priceChange(row.getPriceChange())
                .priceChangeSign(row.getPriceChangeSign())
                .changeRate(row.getChangeRate())
                .marketCap(row.getMarketCap())
                .marketCapRatio(row.getMarketCapRatio())
                .build();
    }


    // 거래대금 순으로 검색한 API 결과 DTO 변환
    private static StockDto.TradingValueRow toTradingValueRow(StockApiDto.TradingValueRow row) {

        return StockDto.TradingValueRow.builder()
                .stockName(row.getStockName())
                .stockCode(row.getStockCode())
                .rank(row.getRank())
                .currentPrice(row.getCurrentPrice())
                .priceChangeSign(row.getPriceChangeSign())
                .priceChange(row.getPriceChange())
                .changeRate(row.getChangeRate())
                .accumulatedTradingValue(row.getAccumulatedTradingValue())
                .averageVolume(row.getAverageVolume())
                .accumulatedVolume(row.getAccumulatedVolume())
                .build();
    }


    // 주식 상세정보 조회 API 결과 DTO 변환
    private static StockDto.Detail toDetail(StockApiDto.Detail detail, String stockName) {

        // [1] Y/N 결과 값을 boolean 으로 변경
        boolean tempStop = Objects.equals(detail.getTempStopYn(), "Y");
        boolean investmentCaution = Objects.equals(detail.getInvestmentCautionYn(), "Y");
        boolean shortOver = Objects.equals(detail.getShortOverYn(), "Y");
        boolean managementIssueCode = Objects.equals(detail.getManagementIssueCode(), "Y");


        return StockDto.Detail.builder()
                .stockName(stockName)
                .stockCode(detail.getStockCode())
                .currentPrice(detail.getCurrentPrice())
                .marketIndexName(detail.getMarketIndexName())
                .sectorName(detail.getSectorName())
                .faceValue(detail.getFaceValue())
                .marketCap(detail.getMarketCap())
                .listedShares(detail.getListedShares())
                .week52High(detail.getWeek52High())
                .week52Low(detail.getWeek52Low())
                .days250High(detail.getDays250High())
                .days250Low(detail.getDays250Low())
                .per(detail.getPer())
                .pbr(detail.getPbr())
                .eps(detail.getEps())
                .bps(detail.getBps())
                .foreignNetBuyQty(detail.getForeignNetBuyQty())
                .programNetBuyQty(detail.getProgramNetBuyQty())
                .foreignOwnershipRate(detail.getForeignOwnershipRate())
                .volumeTurnoverRate(detail.getVolumeTurnoverRate())
                .tempStop(tempStop)
                .investmentCaution(investmentCaution)
                .shortOver(shortOver)
                .managementIssueCode(managementIssueCode)
                .build();
    }


    // 주식 한글명 조회 (Detail 에는 주식 한글명이 없음)
    private static String getStockName(StockApiDto.MarketCapRow marketCapRow,
                                       StockApiDto.TradingValueRow tradingValueRow) {

        return Objects.nonNull(marketCapRow) ? marketCapRow.getStockName() :
                Objects.nonNull(tradingValueRow) ? tradingValueRow.getStockName() : null;
    }


    // 주식 상세정보 조회 API 결과 DTO 변환
    public static StockAiDto.YouTubeRecommendation toYouTubeRecommend(YouTube.Detail detail) {

        return StockAiDto.YouTubeRecommendation.builder()
                .videoId(detail.getVideoId())
                .videoUrl(detail.getVideoUrl())
                .title(detail.getTitle())
                .duration(detail.getDuration().toString())
                .thumbnailUrl(detail.getThumbnailUrl())
                .channelTitle(detail.getChannelTitle())
                .viewCount(detail.getViewCount())
                .likeCount(detail.getLikeCount())
                .build();
    }



}
