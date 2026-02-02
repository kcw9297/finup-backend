package app.finup.layer.domain.indicator.service;

import app.finup.layer.domain.indicator.dto.IndicatorDto;
import app.finup.layer.domain.indicator.enums.FinancialIndexType;
import app.finup.layer.domain.indicator.enums.MarketIndexType;
import app.finup.layer.domain.indicator.redis.IndicatorRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.List;


/**
 * IndicatorService 구현 클래스
 * @author kcw
 * @since 2026-01-14
 */

@Slf4j
@Service
@RequiredArgsConstructor
public class IndicatorServiceImpl implements IndicatorService {

    // 사용
    private final IndicatorRedisStorage indicatorRedisStorage;


    @Override
    public List<IndicatorDto.MarketIndexRow> getMarketIndexes() {

        // [1] 현재 외부에 출력하는 인덱스 목록
        List<MarketIndexType> types = List.of(MarketIndexType.KOSPI, MarketIndexType.KOSDAQ);

        // [2] 조회 및 반환
        return indicatorRedisStorage.getAllMarketIndexes(types);
    }


    @Override
    public List<IndicatorDto.FinancialIndexRow> getFinancialIndexes() {

        // [1] 현재 외부에 출력하는 인덱스 목록
        List<FinancialIndexType> types = List.of(FinancialIndexType.USD, FinancialIndexType.JPY);

        // [2] 조회 및 반환
        return indicatorRedisStorage.getFinancialIndexes(types);
    }

}