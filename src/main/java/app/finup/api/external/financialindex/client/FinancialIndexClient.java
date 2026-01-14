package app.finup.api.external.financialindex.client;

import app.finup.api.external.financialindex.dto.FinancialIndexApiDto;

import java.time.LocalDate;
import java.util.List;


/**
 * 경제 지표 제공 API Client 클래스
 * @author kcw
 * @since 2026-01-14
 */
public interface FinancialIndexClient {


    /**
     * 환율 지표 조회
     * @param date 검색 기준일
     * @return 검색일 기준 환율 조회 결과 DTO 목록
     */
    List<FinancialIndexApiDto.ExchangeRateRow> getExchangeRates(LocalDate date);
}