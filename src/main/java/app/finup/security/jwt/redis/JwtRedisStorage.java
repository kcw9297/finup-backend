package app.finup.security.jwt.redis;

/**
 * Redis 내 JWT 토큰 저장을 로직을 제공하는 인터페이스
 * @author kcw
 * @since 2025-11-26
 */
public interface JwtRedisStorage {

    void save(String jti, String refreshToken);

    String get(String jti);

    void update(String jti, String refreshToken);

    void delete(String jti);
}
