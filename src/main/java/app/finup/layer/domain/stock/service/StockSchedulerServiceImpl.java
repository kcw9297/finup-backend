package app.finup.layer.domain.stock.service;


import app.finup.common.enums.AppStatus;
import app.finup.common.exception.BusinessException;
import app.finup.common.utils.ParallelUtils;
import app.finup.api.external.stock.dto.StockApiDto;
import app.finup.api.external.stock.enums.CandleType;
import app.finup.api.external.stock.client.StockClient;
import app.finup.layer.domain.stock.constant.StockRedisKey;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.dto.StockDtoMapper;
import app.finup.layer.domain.stock.redis.StockRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.util.*;
import java.util.concurrent.ExecutorService;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * StockService 구현 클래스
 * @author kcw
 * @since 2025-12-25
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockSchedulerServiceImpl implements StockSchedulerService {

    // 사용 의존성
    private final StockClient stockClient;
    private final StockRedisStorage stockRedisStorage;

    // 병렬 처리를 위한 의존성
    private final ExecutorService stockApiExecutor;

    // 사용 상수
    private static final Duration DELAY_STOCK_API = Duration.ofMillis(1500);


    @Override
    public String issueToken() {

        // [1] 확인 - 이미 AT가 존재하는 경우 발급 미수행
        if (stockRedisStorage.isExistApiAccessToken()) return null;

        // [2] 주식 실시간 정보를 받기 위한 AT 발급 후, REDIS 내 저장
        StockApiDto.Issue rp = stockClient.issueToken();
        stockRedisStorage.storeApiAccessToken(rp.getAccessToken(), rp.getTtl());
        return rp.getAccessToken();
    }


    @CacheEvict(
            value = {StockRedisKey.CACHE_ANALYZE_CHART, StockRedisKey.CACHE_ANALYZE_DETAIL},
            allEntries = true
    )
    @Override
    public void sync() {

        // [1] AccessToken 조회
        String accessToken = stockRedisStorage.getApiAccessToken();

        // AT가 존재하지 않는 경우 예외 반환
        if (Objects.isNull(accessToken))
            throw new BusinessException(AppStatus.STOCK_AT_NOT_FOUND);

        // [2] 주식 코드 기반 상세정보 조회 후, 동기화할 주식 정보 조회
        List<StockDto.Info> stockInfos = getStockInfoAndMapToInfo(accessToken);

        // [5] 조회 결과를 Redis 내 저장
        stockRedisStorage.storeStockInfos(stockInfos);
    }

    // 동기화할 주식 정보 조회 수행
    private List<StockDto.Info> getStockInfoAndMapToInfo(String accessToken) {

        // [1] 주식 조회 (시가총액 / 거래량 각각 상위 30개 씩)
        // 이후 효율적 조회를 위해 Map으로 변환
        Map<String, StockApiDto.MarketCapRow> marketCapRows =
                toMap(stockClient.getMarketCapRankList(accessToken), StockApiDto.MarketCapRow::getStockCode);

        Map<String, StockApiDto.TradingValueRow> tradingValueRows =
                toMap(stockClient.getTradingValueList(accessToken), StockApiDto.TradingValueRow::getStockCode);

        // [2] 조회된 주식 정보에서 주식코드만 중복 없이 추출하여, 중복 없는 코드정보 Set 생성
        Set<String> stockCodes = toCodeSet(marketCapRows, tradingValueRows);

        // [3] 주식 상세, 차트정보 조회를 모두 병렬로 수행
        // Map<StockCode, DTO>
        Map<String, StockApiDto.Detail> details =
                callAsyncStockApi("주식 상세 정보 조회", stockCodes, stockCode -> stockClient.getDetail(stockCode, accessToken));

        // Map<StockCode, List<DTO>>
        Map<String, List<StockApiDto.Candle>> dayCandles =
                callAsyncStockApi("주식 일봉 정보 조회", stockCodes, stockCode -> stockClient.getCandleList(stockCode, accessToken, CandleType.DAY));

        Map<String, List<StockApiDto.Candle>> weekCandles =
                callAsyncStockApi("주식 주봉 정보 조회", stockCodes, stockCode -> stockClient.getCandleList(stockCode, accessToken, CandleType.WEEK));

        Map<String, List<StockApiDto.Candle>> monthCandles =
                callAsyncStockApi("주식 월봉 정보 조회", stockCodes, stockCode -> stockClient.getCandleList(stockCode, accessToken, CandleType.MONTH));

        // [4] 결과 기반 InfoDTO 생성 및 반환
        return stockCodes.stream()
                .map(stockCode -> toInfo(stockCode, marketCapRows, tradingValueRows, details, dayCandles, weekCandles, monthCandles))
                .toList();
    }

    // 주식 API 호출 병렬 처리
    private <R> Map<String, R> callAsyncStockApi(
            String taskName,
            Set<String> stockCodes,
            Function<String, R> apiFetcher
    ) {

        return ParallelUtils.doParallelTask(
                taskName,
                stockCodes,
                stockCode -> Map.entry(stockCode, apiFetcher.apply(stockCode)),
                ParallelUtils.SEMAPHORE_API_STOCK,
                stockApiExecutor,
                DELAY_STOCK_API

        ).stream().collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue));
    }

    // Info DTO 매핑
    private StockDto.Info toInfo(
            String stockCode,
            Map<String, StockApiDto.MarketCapRow> marketCapRows,
            Map<String, StockApiDto.TradingValueRow> tradingValueRows,
            Map<String, StockApiDto.Detail> details,
            Map<String, List<StockApiDto.Candle>> dayCandles,
            Map<String, List<StockApiDto.Candle>> weekCandles,
            Map<String, List<StockApiDto.Candle>> monthCandles
    ) {

        // [1] 결과 추출
        StockApiDto.MarketCapRow marketCapRow = marketCapRows.get(stockCode);
        StockApiDto.TradingValueRow tradingValueRow = tradingValueRows.get(stockCode);
        StockApiDto.Detail detail = details.get(stockCode);
        List<StockApiDto.Candle> days = dayCandles.get(stockCode);
        List<StockApiDto.Candle> weeks = weekCandles.get(stockCode);
        List<StockApiDto.Candle> months = monthCandles.get(stockCode);

        // [2] 캔들 정보를 모아 차트 DTO로 변환 후, Info DTO 생성
        StockDto.Chart chart = StockDtoMapper.toChart(days, weeks, months);
        return StockDtoMapper.toInfo(chart, detail, marketCapRow, tradingValueRow);
    }


    // Map 변환 메소드
    private <K, V> Map<K, V> toMap(List<V> list, Function<V, K> keyMethod) {

        return list.stream()
                .collect(Collectors.toConcurrentMap(
                        keyMethod,
                        Function.identity()
                ));
    }

    // API 조회결과 중 주식 코드 값을 가진 Set 으로 변환
    private Set<String> toCodeSet(Map<String, StockApiDto.MarketCapRow> marketCapRows,
                                  Map<String, StockApiDto.TradingValueRow> tradingValueRows) {

        // [1] Set 내 Code 추출
        Set<String> marketCapCodes = marketCapRows.keySet();
        Set<String> tradingValueCodes = tradingValueRows.keySet();

        // [2] Code Set 생성 및 반환
        Set<String> resultSet = new HashSet<>();
        resultSet.addAll(marketCapCodes);
        resultSet.addAll(tradingValueCodes);
        return resultSet;
    }

}

