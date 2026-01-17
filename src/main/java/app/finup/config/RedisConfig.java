package app.finup.config;

import app.finup.common.utils.StrUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;
import io.lettuce.core.api.StatefulConnection;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;
import org.redisson.Redisson;
import org.redisson.api.RedissonClient;
import org.redisson.config.Config;
import org.redisson.config.EqualJitterDelay;
import org.redisson.config.SingleServerConfig;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cache.CacheManager;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.cache.RedisCacheConfiguration;
import org.springframework.data.redis.cache.RedisCacheManager;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.connection.RedisStandaloneConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceClientConfiguration;
import org.springframework.data.redis.connection.lettuce.LettuceConnectionFactory;
import org.springframework.data.redis.connection.lettuce.LettucePoolingClientConfiguration;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;
import org.springframework.session.data.redis.config.ConfigureRedisAction;

import java.time.Duration;
import java.util.Objects;

/**
 * Redis Database Config 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Configuration
@EnableCaching // SpringCache 활성화 (Redis)
@RequiredArgsConstructor
public class RedisConfig {

    // Redis
    @Value("${spring.data.redis.host}")
    private String host;

    @Value("${spring.data.redis.port}")
    private int port;

    @Value("${spring.data.redis.password}")
    private String password;

    @Value("${spring.data.redis.ssl.enabled}")
    private boolean ssl;

    @Value("${spring.data.redis.timeout}")
    private int timeout;

    @Value("${spring.data.redis.lettuce.pool.max-active}")
    private int maxActive;

    @Value("${spring.data.redis.lettuce.pool.min-idle}")
    private int minIdle;

    @Value("${spring.data.redis.lettuce.pool.max-idle}")
    private int maxIdle;

    @Value("${spring.data.redis.lettuce.pool.max-wait}")
    private Duration maxWait;

    // Redisson
    @Value("${app.redis.redisson.connection-pool-size}")
    private int redissonPoolSize;

    @Value("${app.redis.redisson.connection-minimum-idle}")
    private int redissonMinIdle;

    @Value("${app.redis.redisson.idle-connection-timeout}")
    private int redissonIdleTimeout;

    @Value("${app.redis.redisson.retry-attempts}")
    private int redissonRetryAttempts;

    @Value("${app.redis.redisson.retry-delay-min}")
    private long redissonRetryDelayMin;

    @Value("${app.redis.redisson.retry-delay-max}")
    private long redissonRetryDelayMax;


    @Bean // ObjectMapper 설정 (원활한 직렬화, 역직렬화 목적으로 커스텀)
    public ObjectMapper objectMapper() {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        objectMapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
        StrUtils.injectObjectMapperBean(objectMapper); // 유틸 클래스에서 사용하기 위해 삽입 (정적 유틸 클래스)
        return objectMapper;
    }

    @Bean // connector 구현체로 Lettuce 사용 (가장 좋은 성능)
    public RedisConnectionFactory redisConnectionFactory() {
        RedisStandaloneConfiguration config = new RedisStandaloneConfiguration();
        config.setHostName(host);
        config.setPort(port);
        if (Objects.nonNull(password) && !password.isBlank()) config.setPassword(password);

        // Lettuce 설정
        // connection pool 설정
        GenericObjectPoolConfig<StatefulConnection<?, ?>> poolConfig = new GenericObjectPoolConfig<>();
        poolConfig.setMaxTotal(maxActive);
        poolConfig.setMaxIdle(maxIdle);
        poolConfig.setMinIdle(minIdle);
        poolConfig.setMaxWait(maxWait);
        poolConfig.setTestOnBorrow(true);
        poolConfig.setTestOnReturn(true);
        poolConfig.setTestWhileIdle(true);

        // lettuce pooling client 설정
        LettucePoolingClientConfiguration.LettucePoolingClientConfigurationBuilder builder =
                LettucePoolingClientConfiguration.builder()
                        .commandTimeout(Duration.ofMillis(timeout))
                        .poolConfig(poolConfig);

        // ssl 설정
        if (ssl) builder.useSsl();

        // 설정 완료
        LettuceClientConfiguration clientConfig = builder.build();
        return new LettuceConnectionFactory(config, clientConfig);
    }


    @Bean(destroyMethod = "shutdown") // redisson 설정
    public RedissonClient redissonClient() {

        // Redisson 설정 객체
        Config config = new Config();

        // Redis 주소 설정
        String protocol = ssl ? "rediss" : "redis"; // SSL 사용 시 rediss://
        String address = "%s://%s:%d".formatted(protocol, host, port);

        // 서버 기본 설정
        SingleServerConfig serverConfig = config.useSingleServer()

                // 서버 주소
                .setAddress(address)

                // Connection Pool 설정
                .setConnectionPoolSize(redissonPoolSize) // 최대 커넥션 수
                .setConnectionMinimumIdleSize(redissonMinIdle) // 최소 유휴 연결 수 (항상 유지할 커넥션 수)
                .setIdleConnectionTimeout(redissonIdleTimeout) // 사용하지 않는 연결 타임아웃

                // Timeout 설정
                .setConnectTimeout(timeout) // Redis 서버 연결 타임아웃
                .setTimeout(timeout) // Redis 명령 응답 타임아웃

                // Retry 설정
                .setRetryAttempts(redissonRetryAttempts) // 최대 재시도 횟수
                .setRetryDelay(new EqualJitterDelay(
                        Duration.ofMillis(redissonRetryDelayMin), // 최소 대기
                        Duration.ofMillis(redissonRetryDelayMax)  // 최대 대기
                ));

        // 비밀번호 설정 (있는 경우만)
        if (Objects.nonNull(password) && !password.isBlank()) serverConfig.setPassword(password);
        return Redisson.create(config);
    }


    @Bean // RedisCacheManager 설정
    public CacheManager cacheManager() {

        // Redis 직렬화 설정
        RedisCacheConfiguration cacheConfig = RedisCacheConfiguration.defaultCacheConfig()
                .entryTtl(Duration.ofHours(6))
                .disableCachingNullValues(); // NULL 캐싱 무효

        return RedisCacheManager.builder(redisConnectionFactory())
                .cacheDefaults(cacheConfig)
                .build();
    }


    @Bean // StringRedisTemplate Bean
    public StringRedisTemplate stringRedisTemplate() {
        return new StringRedisTemplate(redisConnectionFactory());
    }


    @Bean // RedisTemplate Bean
    public RedisTemplate<String, Object> redisTemplate() {
        RedisTemplate<String, Object> redisTemplate = new RedisTemplate<>();
        redisTemplate.setConnectionFactory(redisConnectionFactory());
        redisTemplate.setKeySerializer(new StringRedisSerializer());
        redisTemplate.setValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        redisTemplate.setHashKeySerializer(new StringRedisSerializer());
        redisTemplate.setHashValueSerializer(new GenericJackson2JsonRedisSerializer(objectMapper()));
        return redisTemplate;
    }


    @Bean // CONFIG 비활성화
    public static ConfigureRedisAction configureRedisAction() {
        return ConfigureRedisAction.NO_OP;
    }

}