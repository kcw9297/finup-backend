package app.finup.layer.domain.stock.api;

import app.finup.layer.domain.stock.dto.TokenDto;
import app.finup.layer.domain.stock.redis.AuthTokenStorage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;
import reactor.util.retry.Retry;

import java.time.Duration;
import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class AuthStockApiClientImpl implements AuthStockApiClient {

    @Value("${api.kis.client.id}")
    private String APP_KEY;

    @Value("${api.kis.client.secret}")
    private String APP_SECRET;

    @Qualifier("kisAuthClient")
    private final WebClient kisAuthClient;
    private final AuthTokenStorage authTokenStorage;

    /* api URI */
    public static final String AUTH = "/oauth2/tokenP";


    /**
     * kis 접근 토큰 발급하기
     * Redis에 토큰 있으면 그대로 반환
     * 없거나 만료되었으면 refreshToken() 호출, 새 토큰 발급, Redis 저장 후 반환
     */
    @Override
    public String getToken() {
        String token = authTokenStorage.getToken();
        if (token == null) {
            refreshToken();
            token = authTokenStorage.getToken();
            log.info("kis 접근 토큰 갱신발급함");
        }else{
            log.info("kis 접근 토큰 Redis에서 가져옴");
        }
        return token;
    }

    //kis 접근 토큰 갱신하기
    @Override
    public void refreshToken() {

        TokenDto.Token token = kisAuthClient.post()
                .uri(AUTH)
                .bodyValue(Map.of(
                        "grant_type", "client_credentials",
                        "appkey", APP_KEY,
                        "appsecret", APP_SECRET
                ))
                .retrieve()
                .bodyToMono(TokenDto.Token.class)
                .retryWhen(
                        Retry.backoff(3, Duration.ofMillis(300))
                                .filter(e -> e instanceof WebClientResponseException)
                )
                .block();

        if (token == null || token.getAccessToken() == null) {
            log.error("KIS 접근 토큰 발급 불가");
            throw new IllegalStateException("접근토큰 null");
        }
        authTokenStorage.setToken(token.getAccessToken());
        log.info("kis 접근 토큰 발급 완료");
    }
}
