package app.finup.layer.domain.indicator.redis;


import app.finup.layer.domain.indicator.dto.IndicatorDto;
import app.finup.layer.domain.indicator.enums.FinancialIndexType;
import app.finup.layer.domain.indicator.enums.MarketIndexType;

import java.util.List;

/**
 * 주식 관련 정보를 Redis와 직접 조작하는 기능 제공 Storage 인터페이스
 * @author kcw
 * @since 2025-12-25
 */
public interface IndicatorRedisStorage {


    /**
     * API에서 조회한 경제 지표 정보 일괄 저장
     * @param rows 외부 API에서 조회한 경제지표 DTO 목록
     */
    void storeFinancialIndexes(List<IndicatorDto.FinancialIndexRow> rows);


    /**
     * API에서 조회한 주식 시장 지표 정보 일괄 저장
     * @param rows 외부 API에서 조회한 주식 시장 지표 DTO 목록
     */
    void storeMarketIndexes(List<IndicatorDto.MarketIndexRow> rows);


    /**
     * 경제 지표 일괄 조회
     * @param types 조회할 지표 타입
     * @return 조회된 경제 지표 DTO 목록
     */
    List<IndicatorDto.FinancialIndexRow> getFinancialIndexes(List<FinancialIndexType> types);


    /**
     * 경제 지표 일괄 조회
     *
     * @param types 조회할 지표 타입
     * @return 조회된 경제 지표 DTO 목록
     */
    List<IndicatorDto.MarketIndexRow> getAllMarketIndexes(List<MarketIndexType> types);


}
