package app.finup.layer.domain.stock.scheduler;

import app.finup.infra.news.provider.NewsProvider;
import app.finup.layer.domain.news.dto.NewsDto;
import app.finup.layer.domain.news.redis.NewsRedisStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.annotation.Profile;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.util.List;

@Slf4j
@Component
//@Profile("prod") //잠깐 비활성화
@RequiredArgsConstructor
public class StockNewsScheduler {
    private final NewsProvider newsProvider;
    private final NewsRedisStorage newsRedisStorage;
    private static final Duration TTL_STOCK = Duration.ofMinutes(30);

    @Scheduled(fixedRate = 3 * 60 * 1000)
    public void updateStockNews() {
        List<String> stocks = List.of("삼성전자", "SK하이닉스");

        for (String stockName : stocks){
            refresh(stockName, "date", 10);
            refresh(stockName,"sim", 10);
        }

        log.info("[STOCK_SCHEDULER] 갱신 완료 ({} 종목)", stocks.size());

    }

    private void refresh(String keyword, String category, int limit) {
        String key = "NEWS:STOCK:" + keyword + ":" + category +":" + limit;

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
