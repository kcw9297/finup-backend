package app.finup.api.external.marketindex.client;

import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;


/**
 * 주식 시장 지수 정보 API
 */
public interface MarketIndexClient {
    // 특정 지수와 기준일의 시세 조회
    IndexMarketDto.ApiRow fetchIndex(String indexName, String baseDate);
}