package app.finup.layer.domain.stock.scheduler;

import app.finup.layer.domain.stock.dto.StockDto;
import app.finup.layer.domain.stock.redis.StockStorage;
import app.finup.layer.domain.stock.service.StockAiService;
import app.finup.layer.domain.stock.service.StockService;
import app.finup.layer.domain.stockChart.dto.StockChartDto;
import app.finup.layer.domain.stockChart.dto.StockChartDtoMapper;
import app.finup.layer.domain.stockChart.enums.CandleType;
import app.finup.layer.domain.stockChart.service.StockChartAiService;
import app.finup.layer.domain.stockChart.service.StockChartService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import java.util.*;

/**
 * 종목+ 스케쥴러 화수목금토 새벽 자동 갱신
 * 3시 00분: 시가총액 리스트
 * 3시 10분: 거래대금 리스트
 * 3시 20분: 종목 상세 페이지 종목 데이터
 * 3시 40분: 종목 상세 페이지 종목 AI 데이터
 * 4시 00분: 종목 상세 페이지 종목 추천영상
 * 4시 20분: 종목 상세 페이지 차트 데이터
 * 4시 40분: 종목 상세 페이지 차트 AI 데이터

 * @author lky
 * @since 2025-12-12
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class StockScheduler {

    private final StockService stockService;
    private final StockAiService stockAiService;
    private final StockChartService stockChartService;
    private final StockChartAiService stockChartAiService;
    private final StockStorage stockStorage;


    /**
     * 종목+ 시가총액 리스트
     * 화수목금토 새벽3시 갱신
     */
    @Scheduled(cron = "0 0 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshMarketCapRow(){
        log.info("[SCHEDULER] 종목+ 시가총액 리스트 스케쥴러 실행");
        stockService.refreshMarketCapRow();
    }

    /**
     * 종목+ 거래대금 리스트
     * 화수목금토 새벽 3시 10분 갱신
     */
    @Scheduled(cron = "0 10 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshTradingValueRow(){
        log.info("[SCHEDULER] 종목+ 거래대금 리스트 스케쥴러 실행");
        stockService.refreshTradingValueRow();
    }

    /**
     * 종목 상세 페이지 종목 데이터
     * 화수목금토 새벽 3시 20분 갱신
     */
    @Scheduled(cron = "0 20 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshDetail(){
        log.info("[SCHEDULER] 종목 상세 종목 데이터 스케쥴러 실행");
        Set<String> codes = getCodes();

        for (String code : codes) {
            try {
                stockService.refreshDetail(code);
                Thread.sleep(200);
            } catch (Exception e) {
                log.error("[SCHEDULER] 종목 상세 갱신 실패 code={}", code, e);
            }
        }
    }

    /**
     * 종목 상세 페이지 종목 AI 분석
     * 화수목금토 새벽 3시 40분 갱신
     */
    @Scheduled(cron = "0 40 3 * * TUE,WED,THU,FRI,SAT")
    public void refreshDetailAi(){
        log.info("[SCHEDULER] 종목 상세 종목 AI 분석 스케쥴러 실행");
        Set<String> codes = getCodes();

        for (String code : codes) {
            try {
                StockDto.Detail detail = stockStorage.getDetail(code);
                if (detail == null) {
                    log.warn("[SCHEDULER] detail 데이터 없음 – skip code={}", code);
                    continue;
                }
                stockAiService.refreshDetailAi(code, detail);
                Thread.sleep(300);
            } catch (Exception e) {
                log.error("[SCHEDULER] 종목 상세 AI분석 갱신 실패 code={}", code, e);
            }
        }
    }

    /**
     * 종목 상세 페이지 종목 추천 영상
     * 화수목금토 새벽 4시 갱신
     */
    @Scheduled(cron = "0 0 4 * * TUE,WED,THU,FRI,SAT")
    public void refreshYoutube(){
        log.info("[SCHEDULER] 종목 상세 종목 추천 영상 스케쥴러 실행");
        Set<String> codes = getCodes();
        Set<String> processedKeywords = new HashSet<>();

        for (String code : codes) {
            try {
                Map<String, Object> detailAi = stockStorage.getDetailAi(code);
                if (detailAi == null) {
                    log.warn("[SCHEDULER] detailAi 데이터 없음 – skip code={}", code);
                    continue;
                }
                Object keywordObj = detailAi.get("youtubeKeyword");
                if (!(keywordObj instanceof String keyword) || keyword.isBlank()) {
                    log.warn("[SCHEDULER] youtubeKeyword 없음 – skip code={}", code);
                    continue;
                }
                if (!processedKeywords.add(keyword)) continue;
                stockAiService.refreshYoutube(keyword);
                Thread.sleep(300);
            } catch (Exception e) {
                log.error("[SCHEDULER] 종목 상세 추천영상 갱신 실패 code={}", code, e);
            }
        }
    }

    /**
     * 종목 상세 페이지 차트 데이터
     * 화수목금토 새벽 4시 20분 갱신
     */
    @Scheduled(cron = "0 20 4 * * TUE,WED,THU,FRI,SAT")
    public void refreshChart(){
        log.info("[SCHEDULER] 종목 상세 차트 데이터 스케쥴러 실행");
        Set<String> codes = getCodes();

        for (String code : codes) {
            try {
                CandleType type = CandleType.fromParam("day");
                stockChartService.refreshInquireDaily(code, type);
                Thread.sleep(200);
            } catch (Exception e) {
                log.error("[SCHEDULER] 종목 차트 갱신 실패 code={}", code, e);
            }
        }
    }

    /**
     * 종목 상세 페이지 차트 AI 분석
     * 화수목금토 새벽 4시 40분 갱신
     */
    @Scheduled(cron = "0 40 4 * * TUE,WED,THU,FRI,SAT")
    public void refreshChartAi(){
        log.info("[SCHEDULER] 종목 상세 차트 AI 분석 스케쥴러 실행");
        Set<String> codes = getCodes();

        for (String code : codes) {
            try {
                CandleType type = CandleType.fromParam("day");
                StockChartDto.Row chart = stockChartService.getInquireDaily(code, type);

                StockChartDto.AiInput input = StockChartDtoMapper.toAiInput(
                        type.name(),          // DAY / WEEK / MONTH
                        chart.getOutput()
                );

                if (input == null) {
                    log.warn("[SCHEDULER] input 데이터 없음 – skip code={}", code);
                    continue;
                }
                stockChartAiService.refreshChartAi(code, input);
                Thread.sleep(300);
            } catch (Exception e) {
                log.error("[SCHEDULER] 종목 차트 AI분석 갱신 실패 code={}", code, e);
            }
        }

    }

    private Set<String> getCodes() {
        List<StockDto.MarketCapRow> marketCapList = stockService.getMarketCapRow();
        List<StockDto.TradingValueRow> tradingValueList = stockService.getTradingValueRow();

        Set<String> codes = new HashSet<>();

        for (StockDto.MarketCapRow row : marketCapList) {
            codes.add(row.getMkscShrnIscd());
        }

        for (StockDto.TradingValueRow row : tradingValueList) {
            codes.add(row.getMkscShrnIscd());
        }

        if (codes.isEmpty()) {
            log.warn("[SCHEDULER] 종목 리스트 비어있음");
        }

        return codes;
    }
}
