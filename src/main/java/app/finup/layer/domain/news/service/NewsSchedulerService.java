package app.finup.layer.domain.news.service;

/**
 * 뉴스 동기화 기능 제공 인터페이스
 * @author kcw
 * @since 2025-12-31
 */

public interface NewsSchedulerService {

    /**
     * 메인 기사 목록 동기화
     */
    void syncMain();


    /**
     * 특정 종목 뉴스 기사 동기화
     */
    void syncStock();
}
