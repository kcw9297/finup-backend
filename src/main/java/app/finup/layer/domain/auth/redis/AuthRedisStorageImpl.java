package app.finup.layer.domain.auth.redis;

import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Component
@RequiredArgsConstructor
public class AuthRedisStorageImpl implements AuthRedisStorage {

    private final StringRedisTemplate redisTemplate;

    private static final String CODE_PREFIX = "VERIFY:EMAIL:";
    private static final String VERIFIED_PREFIX = "VERIFIED:EMAIL:";

    private String codeKey(String email) {
        return CODE_PREFIX + email;
    }

    private String verifiedKey(String email) {
        return VERIFIED_PREFIX + email;
    }

    @Override
    public void saveEmailCode(String email, String code, Duration ttl) {
        redisTemplate.opsForValue().set(codeKey(email), code, ttl);
    }

    @Override
    public String getEmailCode(String email) {
        return redisTemplate.opsForValue().get(codeKey(email));
    }

    @Override
    public void removeEmailCode(String email) {
        redisTemplate.delete(codeKey(email));
    }

    @Override
    public boolean isVerified(String email) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(verifiedKey(email)));
    }

    @Override
    public void markVerified(String email, Duration ttl) {
        // 값은 의미 없고, "키 존재" 자체로 인증완료를 표현
        redisTemplate.opsForValue().set(verifiedKey(email), "1", ttl);
    }

    @Override
    public void removeVerified(String email) {
        redisTemplate.delete(verifiedKey(email));
    }
}
