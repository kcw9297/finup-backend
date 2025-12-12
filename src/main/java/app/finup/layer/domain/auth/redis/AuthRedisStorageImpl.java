package app.finup.layer.domain.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AuthRedisStorageImpl implements AuthRedisStorage {

    private final StringRedisTemplate redisTemplate;

    private static final String PREFIX = "VERIFY:EMAIL:";

    private String getKey(String email) {
        return PREFIX + email;
    }

    @Override
    public void saveEmailCode(String email, String code, Duration ttl) {
        redisTemplate.opsForValue().set(getKey(email), code, ttl);
    }

    @Override
    public String getEmailCode(String email) {
        return redisTemplate.opsForValue().get(getKey(email));
    }

    @Override
    public void removeEmailCode(String email) {
        redisTemplate.delete(getKey(email));
    }
}
