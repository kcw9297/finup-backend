package app.finup.infra.redis.manager;

/**
 * Redis ↔ Jwt 간 생명 주기를 관리하는 Manager 클래스
 * @author kcw
 * @since 2025-11-26
 */
public interface RedisJwtManager {

    void save(String jti, String refreshToken);

    String get(String jti);

    void update(String jti, String refreshToken);

    void delete(String jti);
}
