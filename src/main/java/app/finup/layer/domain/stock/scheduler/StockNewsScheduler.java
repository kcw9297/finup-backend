package app.finup.layer.domain.stock.scheduler;

import app.finup.infra.news.provider.NewsProvider;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Slf4j
@Component
//@Profile("prod") //잠깐 비활성화
@RequiredArgsConstructor
public class StockNewsScheduler {
    private final NewsProvider newsProvider;
    private final NewsRedisStorage newsRedisStorage;
    private static final Duration TTL_STOCK = Duration.ofMinutes(30);
    private final StockService stockService;

    @Scheduled(fixedRate = 15 * 60 * 1000)
    @Async("schedulerExecutor")
    public void updateStockNews() {

        List<StockDto.MarketCapRow> marketCapList = stockService.getMarketCapRow();
        List<StockDto.TradingValueRow> tradingValueList = stockService.getTradingValueRow();

        if (marketCapList.isEmpty() && tradingValueList.isEmpty()) {
            log.warn("[SCHEDULER] 종목 리스트 비어있음");
            return;
        }
        Set<String> stockNames = new HashSet<>();
        for(StockDto.MarketCapRow row : marketCapList)
        {
            stockNames.add(row.getHtsKorIsnm());
        }
        for(StockDto.TradingValueRow row : tradingValueList)
        {
            stockNames.add(row.getHtsKorIsnm());
        }
        for (String stockName : stockNames)
        {
            refresh(stockName, "sim", 30);
        }

        log.info("[STOCK_SCHEDULER] 갱신 완료 ({} 종목)", stockNames.size());
    }

    private void refresh(String keyword, String category, int limit) {
        String key = "NEWS:STOCK:" + keyword +":" + limit;

        List<NewsDto.Row> fresh = newsProvider.fetchStockNews(keyword, category, limit);

        if(isComplete(fresh)){
            newsRedisStorage.saveNews(key, fresh, TTL_STOCK);
            log.info("[STOCK_SCHEDULER] SAVE {}", key);
        }else{
            log.warn("[STOCK_SCHEDULER] SKIP SAVE (incomplete) {}", key);
        }
    }

    private boolean isComplete(List<NewsDto.Row> list) {
        return list != null && list.size() >= 5;
    }
}
