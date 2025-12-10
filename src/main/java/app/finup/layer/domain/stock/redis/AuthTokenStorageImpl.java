package app.finup.layer.domain.stock.redis;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.time.Duration;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthTokenStorageImpl implements AuthTokenStorage {

    private final StringRedisTemplate srt;

    @Override
    public void setToken(String token) {
        srt.opsForValue().set("token", token, Duration.ofHours(23));
    }

    @Override
    public String getToken() {
        return srt.opsForValue().get("token");
    }
}
