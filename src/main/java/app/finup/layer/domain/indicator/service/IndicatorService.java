package app.finup.layer.domain.indicator.service;

import app.finup.layer.domain.indicator.dto.IndicatorDto;

import java.util.List;


/**
 * 경제 지표 로직 관리 인터페이스
 * @author kcw
 * @since 2026-01-14
 */
public interface IndicatorService {

    /**
     * 주식 시장 지표 목록 조회
     * @return 조회된 지표 DTO 목록
     */
    List<IndicatorDto.MarketIndexRow> getMarketIndexes();


    /**
     * 금융 지표 목록 조회
     * @return 조회된 지표 DTO 목록
     */
    List<IndicatorDto.FinancialIndexRow> getFinancialIndexes();

}