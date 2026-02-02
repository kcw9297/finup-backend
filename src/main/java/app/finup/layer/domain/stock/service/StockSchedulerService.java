package app.finup.layer.domain.stock.service;

/**
 * 주식 스케줄러에서 처리할 로직을 제공하는 인터페이스
 * @author kcw
 * @since 2025-12-25
 */
public interface StockSchedulerService {

    /**
     * 주식 정보 제공을 위한 토큰 발급
     * @return 발급받은 API AccessToken 문자열
     */
    String issueToken();

    /**
     * 주식 정보 동기화
     */
    void sync();
}
