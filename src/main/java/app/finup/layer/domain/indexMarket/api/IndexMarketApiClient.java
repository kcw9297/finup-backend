package app.finup.layer.domain.indexMarket.api;

import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;

public interface IndexMarketApiClient {
    // 특정 지수와 기준일의 시세 조회
    IndexMarketDto.ApiRow fetchIndex(String indexName, String baseDate);
}