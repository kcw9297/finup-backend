package app.finup.layer.domain.indexMarket.service;

import app.finup.layer.domain.indexMarket.dto.IndexMarketDto;
import java.util.List;

public interface IndexMarketService {
    // 오늘 지수, 대비 %
    List<IndexMarketDto.Row> getLatestIndexes();

    // 스케줄러용 (저장)
    void updateIndexes();
}