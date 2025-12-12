package app.finup.layer.domain.stock.scheduler;

import app.finup.layer.domain.stock.api.StockApiClient;
import app.finup.layer.domain.stock.service.StockAiService;
import app.finup.layer.domain.stock.service.StockService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

/**
 * 종목+ 스케쥴러 자동 갱신
 * @author lky
 * @since 2025-12-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {

    private final StockService stockService;
    private final StockApiClient stockApiClient;
    private final StockAiService stockAiService;

    /**
     * 종목+ 시가총액 리스트
     * 화수목금토 새벽3시 갱신
     */
    @Scheduled(cron = "0 0 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshMarketCapRow(){
        log.info("[SCHEDULER] 종목+ 시가총액 리스트 스케쥴러 실행");

    }

    /**
     * 종목 상세 페이지 종목 데이터
     * 화수목금토 새벽 3시 30분 갱신
     */
    @Scheduled(cron = "0 30 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshDetail(){
        log.info("[SCHEDULER] 종목 상세 종목 데이터 스케쥴러 실행");
    }

    /**
     * 종목 상세 페이지 종목 AI 분석
     * 화수목금토 새벽 3시 45분 갱신
     */
    @Scheduled(cron = "0 45 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshDetailAi(){
        log.info("[SCHEDULER] 종목 상세 종목 AI 분석 스케쥴러 실행");
    }

    /**
     * 종목 상세 페이지 종목 추천 영상
     * 화수목금토 새벽 4시 갱신
     */
    @Scheduled(cron = "0 0 4 * * TUE,WED,THU,FRI,SAT")
    public void refreshYoutube(){
        log.info("[SCHEDULER] 종목 상세 종목 추천 영상 스케쥴러 실행");
    }
}
