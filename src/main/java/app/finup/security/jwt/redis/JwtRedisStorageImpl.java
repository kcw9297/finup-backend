package app.finup.security.jwt.redis;

import app.finup.common.constant.Const;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

/**
 * RedisJwtStorage 구현체
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtRedisStorageImpl implements JwtRedisStorage {

    private final StringRedisTemplate srt;

    @Value("${jwt.expiration.refresh-token}")
    public Duration expirationRt;

    @Override
    public void save(String jti, String refreshToken) {
        srt.opsForValue().set(createJwtKey(jti), refreshToken, expirationRt);
    }

    @Override
    public String get(String jti) {
        return srt.opsForValue().get(createJwtKey(jti));
    }

    @Override
    public void update(String jti, String refreshToken) {
        srt.opsForValue().setIfPresent(createJwtKey(jti), refreshToken);
    }

    @Override
    public void delete(String jti) {
        srt.delete(createJwtKey(jti));
    }


    // jwt redis key 생성
    public static String createJwtKey(String jti) {
        return "%s%s".formatted(Const.PREFIX_KEY_JWT, jti);
    }
}
