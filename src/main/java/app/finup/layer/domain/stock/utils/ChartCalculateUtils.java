package app.finup.layer.domain.stock.utils;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.UtilsException;
import app.finup.layer.domain.stock.dto.StockAiDto;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.enums.ChartType;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.List;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class ChartCalculateUtils {

    /**
     * 차트 분석에 필요한 수치 계산
     */
    public static StockAiDto.ChartMetrics calculate(ChartType chartType, List<StockDto.Candle> candles) {

        // candleType별 구간 설정
        int recentCount;
        boolean excludeLast; // 마지막 캔들 제외 여부 (미완성)

        switch (chartType) {
            case DAY -> {
                recentCount = 5;
                excludeLast = false;
            }
            case WEEK -> {
                recentCount = 4;
                excludeLast = true; // 마지막 주봉은 미완성
            }
            case MONTH -> {
                recentCount = 3;
                excludeLast = true; // 마지막 월봉은 미완성
            }
            default -> throw new UtilsException(AppStatus.UTILS_LOGIC_FAILED);
        }

        // 미완성 캔들 제외 처리
        List<StockDto.Candle> targetCandles = excludeLast
                ? candles.subList(0, candles.size() - 1)
                : candles;

        int totalSize = targetCandles.size();

        // 최근 구간 / 이전 구간 분리
        List<StockDto.Candle> recentCandles = targetCandles.subList(
                Math.max(0, totalSize - recentCount), totalSize);
        List<StockDto.Candle> pastCandles = targetCandles.subList(
                0, Math.max(0, totalSize - recentCount));

        // 평균 계산
        double recentAvgPrice = avgClosePrice(recentCandles);
        double pastAvgPrice = avgClosePrice(pastCandles);
        double recentAvgVolume = avgVolume(recentCandles);
        double pastAvgVolume = avgVolume(pastCandles);

        // 변화율 계산
        double priceChangeRate = calculateChangeRate(pastAvgPrice, recentAvgPrice);
        double volumeChangeRate = calculateChangeRate(pastAvgVolume, recentAvgVolume);

        // 현재 위치 계산 (30개 캔들 중 상대적 위치)
        long currentPrice = candles.get(candles.size() - 1).getClosePrice();
        String pricePosition = calculatePricePosition(candles, currentPrice);

        // 계산 결과 반환
        return StockAiDto.ChartMetrics.builder()
                .priceChangeRate(priceChangeRate)
                .volumeChangeRate(volumeChangeRate)
                .pricePosition(pricePosition)
                .build();
    }

    // 종가 평균 계산
    private static double avgClosePrice(List<StockDto.Candle> candles) {
        return candles.stream()
                .mapToLong(StockDto.Candle::getClosePrice)
                .average()
                .orElse(0);
    }

    // 평균 거래량 계산
    private static double avgVolume(List<StockDto.Candle> candles) {
        return candles.stream()
                .mapToLong(StockDto.Candle::getAccumulatedVolume)
                .average()
                .orElse(0);
    }

    // 변동률 계산
    private static double calculateChangeRate(double past, double recent) {
        if (past == 0) return 0;
        return ((recent - past) / past) * 100;
    }


    // 현재 가격이 30개 캔들 중 어디에 위치하는지 판단
    private static String calculatePricePosition(List<StockDto.Candle> candles, long currentPrice) {

        // 최대, 최소 값 계산
        long maxPrice = candles.stream().mapToLong(StockDto.Candle::getHighPrice).max().orElse(0);
        long minPrice = candles.stream().mapToLong(StockDto.Candle::getLowPrice).min().orElse(0);

        // 최대/최소가격이 같은 경우
        if (maxPrice == minPrice) return "중간";

        // 위치 계산 후, 위치 값 반환
        double position = (double) (currentPrice - minPrice) / (maxPrice - minPrice) * 100;
        if (position >= 80) return "상위 20% (고점 근처)";
        if (position >= 60) return "상위 40%";
        if (position >= 40) return "중간";
        if (position >= 20) return "하위 40%";
        return "하위 20% (저점 근처)";
    }


    // 차트 정보 조회
    public static List<StockDto.Candle> getCandles(ChartType chartType, StockDto.Info stockInfo) {

        // [1] 주식 정보 내 차트 정보 조회
        StockDto.Chart chart = stockInfo.getChart();

        // [2] 차트 정보 내, 특정 기준 캔들 목록 반환
        return switch (chartType) {
            case DAY    -> chart.getDayCandles();
            case WEEK   -> chart.getWeekCandles();
            case MONTH  -> chart.getMonthCandles();
        };
    }
}
