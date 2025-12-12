package app.finup.layer.domain.auth.redis;

import java.time.Duration;

public interface AuthRedisStorage {

    void saveEmailCode(String email, String code, Duration ttl);

    String getEmailCode(String email);

    void removeEmailCode(String email);
}
