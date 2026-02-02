package app.finup.layer.domain.indicator.service;


/**
 * 경제 지표 스케줄링 메소드 관리 인터페이스
 * @author kcw
 * @since 2026-01-14
 */
public interface IndicatorSchedulerService {

    /**
     * 경제 지표 동기화 (환율, ...)
     */
    void syncFinancialIndex();


    /**
     * 주식 시장 지표 동기화 (코스피, 코스닥, ...)
     */
    void syncMarketIndex();
}