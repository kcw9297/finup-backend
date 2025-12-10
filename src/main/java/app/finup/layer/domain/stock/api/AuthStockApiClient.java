package app.finup.layer.domain.stock.api;

/**
 * KIS API 사용을 위한 접큰 토큰 발급 인터페이스
 * @author lky
 * @since 2025-12-10
 */
public interface AuthStockApiClient {
    String getToken();
    void refreshToken();
}
