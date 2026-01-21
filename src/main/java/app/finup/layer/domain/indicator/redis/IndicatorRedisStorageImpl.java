package app.finup.layer.domain.indicator.redis;

import app.finup.common.utils.StrUtils;
import app.finup.layer.base.template.RedisCodeTemplate;
import app.finup.layer.domain.indicator.constant.IndicatorRedisKey;
import app.finup.layer.domain.indicator.dto.IndicatorDto;
import app.finup.layer.domain.indicator.enums.FinancialIndexType;
import app.finup.layer.domain.indicator.enums.MarketIndexType;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * IndicatorRedisStorage 구현 클래스
 * @author kcw
 * @since 2026-01-25
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class IndicatorRedisStorageImpl implements IndicatorRedisStorage {

    // 사용 의존성
    private final StringRedisTemplate srt;

    // 사용 상수
    private static final Duration TTL = Duration.ofDays(7); // 순위 정보


    @Override
    public void storeFinancialIndexes(List<IndicatorDto.FinancialIndexRow> rows) {

        // [1] Hash에 담을 데이터 형태로 변환 (Map<지표명, JSON>)
        Map<String, String> keyValueMap = rows.stream()
                .collect(Collectors.toMap(
                        dto -> dto.getIndexName().replace("/KRW", ""),
                        StrUtils::toJson,
                        (existing, replacement) -> replacement
                ));

        // [2] Hash 삽입 수행
        RedisCodeTemplate.putAllHash(srt, IndicatorRedisKey.KEY_INDEX_FINANCIAL, keyValueMap, TTL);
    }


    @Override
    public void storeMarketIndexes(List<IndicatorDto.MarketIndexRow> rows) {

        // [1] Hash에 담을 데이터 형태로 변환 (Map<지표명, JSON>)
        Map<String, String> keyValueMap = rows.stream()
                .filter(row -> !Objects.equals(row.getIndexName(), "IT 서비스")) // 필요하지 않은 지표이면서 중복 정보
                .collect(Collectors.toMap(
                        IndicatorDto.MarketIndexRow::getIndexName,
                        StrUtils::toJson,
                        (existing, replacement) -> replacement
                ));

        // [2] Hash 삽입 수행
        RedisCodeTemplate.putAllHash(srt, IndicatorRedisKey.KEY_INDEX_MARKET, keyValueMap, TTL);
    }


    @Override
    public List<IndicatorDto.FinancialIndexRow> getFinancialIndexes(List<FinancialIndexType> types) {

        // [1] enum 내 value 정보 기반 조회
        List<String> keys = types.stream().map(FinancialIndexType::name).toList();

        // [2] 조회 및 반환
        return RedisCodeTemplate.getMultiHash(
                srt,
                IndicatorRedisKey.KEY_INDEX_FINANCIAL,
                keys,
                IndicatorDto.FinancialIndexRow.class
        );
    }


    @Override
    public List<IndicatorDto.MarketIndexRow> getAllMarketIndexes(List<MarketIndexType> types) {

        // [1] enum 내 value 정보 기반 조회
        List<String> keys = types.stream().map(MarketIndexType::getValue).toList();

        // [2] 조회 및 반환
        return RedisCodeTemplate.getMultiHash(
                srt,
                IndicatorRedisKey.KEY_INDEX_MARKET,
                keys,
                IndicatorDto.MarketIndexRow.class
        );
    }

}
