package app.finup.infra.redis.manager;

import app.finup.infra.redis.utils.RedisUtils;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class RedisJwtManagerImpl implements RedisJwtManager {

    private final StringRedisTemplate srt;

    @Value("${jwt.expiration.refresh-token}")
    public Duration expirationRt;

    @Override
    public void save(String jti, String refreshToken) {
        srt.opsForValue().set(RedisUtils.createJwtKey(jti), refreshToken, expirationRt);
    }

    @Override
    public String get(String jti) {
        return srt.opsForValue().get(RedisUtils.createJwtKey(jti));
    }

    @Override
    public void update(String jti, String refreshToken) {
        srt.opsForValue().setIfPresent(RedisUtils.createJwtKey(jti), refreshToken);
    }

    @Override
    public void delete(String jti) {
        srt.delete(RedisUtils.createJwtKey(jti));
    }
}
