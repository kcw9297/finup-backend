package app.finup.config;

import io.netty.channel.ChannelOption;
import io.netty.handler.timeout.ReadTimeoutHandler;
import io.netty.handler.timeout.WriteTimeoutHandler;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.reactive.ReactorClientHttpConnector;
import org.springframework.web.reactive.function.client.ExchangeStrategies;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.netty.http.client.HttpClient;

import java.time.Duration;
import java.util.concurrent.TimeUnit;

/**
 * API 별 사용 WebClient Bean을 등록하기 위한 설정 클래스
 * @author kcw
 * @since 2025-12-05
 */

@Configuration(proxyBeanMethods = false)
public class WebClientConfig {

    // API URL
    private static final String ENDPOINT_YOUTUBE = "https://www.googleapis.com/youtube/v3";
    private static final String ENDPOINT_NAVER_NEWS = "https://openapi.naver.com";
    private static final String ENDPOINT_KEXIM = "https://oapi.koreaexim.go.kr/site/program/financial/exchangeJSON";
    private static final String ENDPOINT_KIS = "https://openapi.koreainvestment.com:9443";
    private static final String ENDPOINT_OPEN_PORTAL = "https://apis.data.go.kr/1160100/service/GetMarketIndexInfoService/getStockMarketIndex";

    // 필요 상수
    @Value("${api.naver-news.client.id}")
    private String naverClientId;

    @Value("${api.naver-news.client.secret}")
    private String naverClientSecret;

    @Value("${api.kis.client.id}")
    private String kisAppKey;

    @Value("${api.kis.client.secret}")
    private String kisAppSecret;


    @Bean(name = "youTubeClient") // 유튜브 API 사용을 위한 Client
    public WebClient youTubeClient() {

        // 버퍼 크기를 16MB로 증가
        // DataBufferLimitException: Exceeded limit on max bytes to buffer 예외 발생으로 인한 버퍼 증가 처리
        ExchangeStrategies strategies = ExchangeStrategies.builder()
                .codecs(configurer -> configurer
                        .defaultCodecs()
                        .maxInMemorySize(16 * 1024 * 1024)) // 16MB
                .build();

        return WebClient.builder()
                .baseUrl(ENDPOINT_YOUTUBE)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .exchangeStrategies(strategies)
                .build();
    }

    @Bean(name = "naverClient") // 네이버 OpenAPI 사용을 위한 Client
    public WebClient naverClient() {
        return WebClient.builder()
                .baseUrl(ENDPOINT_NAVER_NEWS)
                .defaultHeader("X-Naver-Client-Id", naverClientId)
                .defaultHeader("X-Naver-Client-Secret", naverClientSecret)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();
    }

    @Bean(name="kisClient")
    public WebClient kisClient(){
        return WebClient.builder()
                .baseUrl(ENDPOINT_KIS)
                .defaultHeader("appkey", kisAppKey)
                .defaultHeader("appsecret", kisAppSecret)
                .build();
    }

    @Bean(name="kisAuthClient")
    public WebClient kisAuthClient(){
        return WebClient.builder()
                .baseUrl(ENDPOINT_KIS)
                .build();
    }


    @Bean(name="keximClient")
    public WebClient keximClient() {
        return WebClient.builder()
                .baseUrl(ENDPOINT_KEXIM)
                .build();
    }

    @Bean(name="openPortalClient")
    public WebClient openPortalClient() {
        return WebClient.builder()
                .baseUrl(ENDPOINT_OPEN_PORTAL)
                .build();
    }

    @Bean(name="oauth2GoogleClient")
    public WebClient oauth2GoogleClient() {

        // HttpClient 설정 (타임아웃)
        HttpClient httpClient = HttpClient.create() // 응답 타임아웃 : 5초
                .option(ChannelOption.CONNECT_TIMEOUT_MILLIS, 5000)
                .responseTimeout(Duration.ofSeconds(5))
                .doOnConnected(conn -> conn
                        .addHandlerLast(new ReadTimeoutHandler(5, TimeUnit.SECONDS))
                        .addHandlerLast(new WriteTimeoutHandler(5, TimeUnit.SECONDS))
                );

        return WebClient.builder()
                .clientConnector(new ReactorClientHttpConnector(httpClient))
                .build();
    }
}