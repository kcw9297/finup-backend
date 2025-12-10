package app.finup.layer.domain.stock.redis;

/**
 * KIS API 사용을 위한 접큰 토큰 저장 인터페이스
 * @author lky
 * @since 2025-12-10
 */
public interface AuthTokenStore {
    void setToken(String token);
    String getToken();
}
