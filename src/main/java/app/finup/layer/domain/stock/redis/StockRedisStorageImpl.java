package app.finup.layer.domain.stock.redis;


import app.finup.common.utils.StrUtils;
import app.finup.layer.base.template.RedisCodeTemplate;
import app.finup.layer.domain.stock.constant.StockRedisKey;
import app.finup.layer.domain.stock.dto.StockAiDto;
import app.finup.layer.domain.stock.dto.StockDto;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.HashOperations;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * StockRedisStorage 구현 클래스
 * @author kcw
 * @since 2025-12-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockRedisStorageImpl implements StockRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final int MAX_AMOUNT_RECOMMEND_YOUTUBE = 5;
    private static final Duration TTL_RANK = Duration.ofDays(2); // 순위 정보
    private static final Duration TTL_INFO = Duration.ofDays(2); // 종목 상세 정보
    private static final Duration TTL_ANALYZE = Duration.ofHours(1); // 종목 상세 정보
    private static final Duration TTL_RECOMMEND_YOUTUBE = Duration.ofHours(1); // 종목 상세 정보


    @Override
    public void storeStockCodeNames(Map<String, String> codeNameMap) {
        srt.opsForHash().putAll(StockRedisKey.KEY_NAMES, codeNameMap);
    }


    @Override
    public boolean isExistStockName() {
        return srt.hasKey(StockRedisKey.KEY_NAMES);
    }


    @Override
    public void storeApiAccessToken(String accessToken, Duration ttl) {
        srt.opsForValue().set(StockRedisKey.KEY_AT, accessToken, ttl);
    }


    @Override
    public String getApiAccessToken() {
        return srt.opsForValue().get(StockRedisKey.KEY_AT);
    }


    @Override
    public boolean isExistApiAccessToken() {
        return srt.hasKey(StockRedisKey.KEY_AT);
    }


    @Override
    public void storeStockInfos(List<StockDto.Info> stockInfos) {

        // [1] 순위 정보 저장
        removeAndStoreMarketCap(stockInfos);
        removeAndStoreTradingValue(stockInfos);

        // [2] 종목 상세 정보 저장
        removeAndStoreInfo(stockInfos);
    }


    // 시가총액 순 정보 저장
    private void removeAndStoreMarketCap(List<StockDto.Info> stockInfos) {

        removeAndStoreRank(
                StockRedisKey.KEY_RANK_MARKET_CAP,
                stockInfos,
                StockDto.Info::getMarketCap,              // Info → MarketCapRow
                StockDto.MarketCapRow::getStockCode,      // Row → code
                StockDto.MarketCapRow::getRank            // Row → rank
        );
    }


    // 거래대금 순 정보 저장
    private void removeAndStoreTradingValue(List<StockDto.Info> stockInfos) {

        removeAndStoreRank(
                StockRedisKey.KEY_RANK_TRADING_VALUE,
                stockInfos,
                StockDto.Info::getTradingValue,           // Info → TradingValueRow
                StockDto.TradingValueRow::getStockCode,   // Row → code
                StockDto.TradingValueRow::getRank         // Row → rank
        );
    }


    // 기존 순위 정보를 삭제하고, 새로운 순위 정보 삽입
    private <T> void removeAndStoreRank(String key,
                                        List<StockDto.Info> stockInfos,
                                        Function<StockDto.Info, T> extractor,
                                        Function<T, String> codeGetter,
                                        Function<T, Integer> rankGetter) {

        // [1] 기존 데이터 삭제
        srt.delete(key);

        // [2] 저장할 순위 별 목록 생성
        ZSetOperations<String, String> zSetOps = srt.opsForZSet();
        stockInfos.stream()
                .map(extractor)
                .filter(Objects::nonNull)
                .forEach(row -> zSetOps.add(key, codeGetter.apply(row), rankGetter.apply(row)));

        // [3] 상세정보 목록 삽입
        srt.expire(key, TTL_RANK);
    }


    // 종목 상세정보 저장
    private void removeAndStoreInfo(List<StockDto.Info> stockInfos) {

        // [1] 저장할 상세정보 목록 생성
        HashOperations<String, Object, Object> hashOps = srt.opsForHash();
        Map<String, String> infoMap = stockInfos.stream()
                .collect(Collectors.toConcurrentMap(
                        info -> info.getDetail().getStockCode(),
                        StrUtils::toJson
                ));

        // [2] 기존 상세정보 목록 삭제 후 삽입
        srt.delete(StockRedisKey.KEY_INFOS);
        hashOps.putAll(StockRedisKey.KEY_INFOS, infoMap);
        srt.expire(StockRedisKey.KEY_INFOS, TTL_INFO);
    }


    @Override
    public StockDto.Info getStockInfo(String stockCode) {

        // [1] Redis 조회
        Object objInfo = srt.opsForHash().get(StockRedisKey.KEY_INFOS, stockCode);

        // [2] DTO 변환 및 반환
        return Objects.isNull(objInfo) ? null : StrUtils.fromJson((String) objInfo, StockDto.Info.class);
    }


    @Override
    public List<StockDto.Info> getAllStockInfos() {

        // [1] Redis 내 조회
        Map<Object, Object> entries = srt.opsForHash().entries(StockRedisKey.KEY_INFOS);

        // [2] DTO 변환 및 반환
        return entries.values()
                .stream()
                .map(infoObj -> StrUtils.fromJson((String) infoObj, StockDto.Info.class))
                .filter(Objects::nonNull)
                .toList();
    }


    @Override
    public List<StockDto.Info> getMarketCapStockInfos() {
        return getSortedStockInfos(StockRedisKey.KEY_RANK_MARKET_CAP);
    }


    @Override
    public List<StockDto.Info> getTradingValueStockInfos() {
        return getSortedStockInfos(StockRedisKey.KEY_RANK_TRADING_VALUE);
    }


    // 순위로 정렬된 주식 상세정보 목록 조회
    private List<StockDto.Info> getSortedStockInfos(String key) {

        // [1] 시가총액 순으로 정렬된 Code Set 조회
        Set<String> stockCodes = srt.opsForZSet().range(key, 0, -1);

        // 만약 null인 경우는 빈 리스트로 반환
        if (Objects.isNull(stockCodes) || stockCodes.isEmpty()) return List.of();

        // [2] 코드에 대응하는 주식정보 일괄 조회
        HashOperations<String, Object, Object> hashOps = srt.opsForHash();
        List<Object> objInfos = hashOps.multiGet(StockRedisKey.KEY_INFOS, new ArrayList<>(stockCodes));

        // [3] 타입 캐스팅 및 반환
        return objInfos.stream()
                .filter(Objects::nonNull)
                .map(objInfo -> StrUtils.fromJson((String) objInfo, StockDto.Info.class))
                .toList();
    }


    @Override
    public void storePrevChartAnalyze(String stockCode, Long memberId, StockAiDto.ChartAnalyzation analyzation) {

        RedisCodeTemplate.storeJsonValue(
                srt,
                getKey(StockRedisKey.KEY_ANALYZE_CHART, stockCode, memberId),
                analyzation,
                TTL_ANALYZE
        );
    }


    @Override
    public StockAiDto.ChartAnalyzation getPrevChartAnalyze(String stockCode, Long memberId) {

        return RedisCodeTemplate.getJsonValue(
                srt,
                getKey(StockRedisKey.KEY_ANALYZE_CHART, stockCode, memberId),
                StockAiDto.ChartAnalyzation.class
        );
    }


    @Override
    public void storePrevDetailAnalyze(String stockCode, Long memberId, StockAiDto.DetailAnalyzation analyzation) {

        RedisCodeTemplate.storeJsonValue(
                srt,
                getKey(StockRedisKey.KEY_ANALYZE_DETAIL, stockCode, memberId),
                analyzation,
                TTL_ANALYZE
        );
    }


    @Override
    public StockAiDto.DetailAnalyzation getPrevDetailAnalyze(String stockCode, Long memberId) {

        return RedisCodeTemplate.getJsonValue(
                srt,
                getKey(StockRedisKey.KEY_ANALYZE_DETAIL, stockCode, memberId),
                StockAiDto.DetailAnalyzation.class
        );
    }


    @Override
    public void storePrevRecommendedVideoIds(String stockCode, Long memberId, List<String> videoIds) {

        // [1] key 생성
        String key = getKey(StockRedisKey.KEY_RECOMMEND_YOUTUBE, stockCode, memberId);

        // [2] 저장 수행
        RedisCodeTemplate.addPrevList(srt, key, videoIds, MAX_AMOUNT_RECOMMEND_YOUTUBE, TTL_RECOMMEND_YOUTUBE);
    }


    @Override
    public List<String> getPrevRecommendedVideoIds(String stockCode, Long memberId) {

        // [1] key 생성
        String key = getKey(StockRedisKey.KEY_RECOMMEND_YOUTUBE, stockCode, memberId);

        // [2] 저장 수행
        return RedisCodeTemplate.getPrevList(srt, key);
    }


    // key 조립 수행
    private String getKey(String baseKey, String stockCode, Long memberId) {

        return StrUtils.fillPlaceholder(
                baseKey,
                Map.of(StockRedisKey.STOCK_CODE, stockCode, StockRedisKey.MEMBER_ID, String.valueOf(memberId))
        );
    }





}
