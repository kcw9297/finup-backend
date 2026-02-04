package app.finup.layer.domain.stock.service;

import app.finup.api.external.stock.dto.StockApiDto;
import app.finup.api.external.stock.client.StockClient;
import app.finup.infra.file.provider.XlsxProvider;
import app.finup.infra.file.storage.FileStorage;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.redis.StockRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;

/**
 * StockService 구현 클래스
 * @author kcw
 * @since 2025-12-25
 */
@Slf4j
@Service
@Transactional
@RequiredArgsConstructor
public class StockServiceImpl implements StockService {

    // 사용 의존성
    private final StockClient stockClient;
    private final StockRedisStorage stockRedisStorage;
    private final FileStorage fileStorage;
    private final XlsxProvider xlsxProvider;


    @Override
    public String issueToken() {

        // [1] 주식 실시간 정보를 받기 위한 AT 발급
        StockApiDto.Issue rp = stockClient.issueToken();

        // [2] Redis 내 발급 토큰 저장
        stockRedisStorage.storeApiAccessToken(rp.getAccessToken(), rp.getTtl());
        return rp.getAccessToken();
    }


    @Override
    public List<StockDto.MarketCapRow> getMarketCapList() {
        return stockRedisStorage.getMarketCapStockInfos()
                .stream()
                .map(StockDto.Info::getMarketCap)
                .toList();
    }


    @Override
    public List<StockDto.TradingValueRow> getTradingValueList() {
        return stockRedisStorage.getTradingValueStockInfos()
                .stream()
                .map(StockDto.Info::getTradingValue)
                .toList();
    }


    @Override
    public StockDto.Chart getChart(String stockCode) {
        return stockRedisStorage.getStockInfo(stockCode).getChart();
    }


    @Override
    public StockDto.Detail getDetail(String stockCode) {
        return stockRedisStorage.getStockInfo(stockCode).getDetail();
    }

}


