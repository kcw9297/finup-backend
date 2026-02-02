package app.finup.api.external.marketindex.client;

import app.finup.api.external.marketindex.dto.MarketIndexApiDto;

import java.time.LocalDate;
import java.util.List;


/**
 * 주식 시장 지수 API Client 클래스
 * @author kcw
 * @since 2026-01-14
 */
public interface MarketIndexClient {

    /**
     * 주식 시장 지수 일괄 조회 (단일)
     * @param date 기준일
     * @return API에서 제공하는 모든 지수 정보 목록
     */
    List<MarketIndexApiDto.Row> getIndexList(LocalDate date);
}