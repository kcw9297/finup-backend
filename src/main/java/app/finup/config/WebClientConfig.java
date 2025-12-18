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
    @Value("${api.youtube.endpoint}")
    private String youtubeEndpoint;

    @Value("${api.naver-news.endpoint}")
    private String naverEndPoint;

    @Value(("${api.finance-dict.endpoint}"))
    private String dataPortalEndPoint;

    @Value("${API_KEXIM_ENDPOINT}")
    private String keximEndpoint;

    @Value("${API_OPENPORTAL_ENDPOINT}")
    private String openPortalEndpoint;

    // 필요 상수
    @Value("${api.naver-news.client.id}")
    private String naverClientId;

    @Value("${api.naver-news.client.secret}")
    private String naverClientSecret;

    @Value(("${api.kis.endpoint}"))
    private String kisEndPoint;

    @Value(("${api.kis.client.id}"))
    private String kisAppKey;

    @Value(("${api.kis.client.secret}"))
    private String kisAppSecret;

    @Value(("${api.finance-dict.key}"))
    private String dataPortalSecret;

    @Value("${API_KEXIM_KEY}")
    private String keximKey;

    @Value("${API_OPENPORTAL_KEY}")
    private String openPortalKey;


    @Bean(name = "youTubeClient") // 유튜브 API 사용을 위한 Client
    public WebClient youTubeClient() {
        return WebClient.builder()
                .baseUrl(youtubeEndpoint)
                .defaultHeader(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE)
                .build();
    }

    @Bean(name = "naverClient") // 네이버 OpenAPI 사용을 위한 Client
    public WebClient naverClient() {
        return WebClient.builder()
                .baseUrl(naverEndPoint)
                .defaultHeader("X-Naver-Client-Id", naverClientId)
                .defaultHeader("X-Naver-Client-Secret", naverClientSecret)
                .defaultHeader(HttpHeaders.USER_AGENT, "Mozilla/5.0")
                .build();
    }

    @Bean(name="kisClient")
    public WebClient kisClient(){
        return WebClient.builder()
                .baseUrl(kisEndPoint)
                .defaultHeader("appkey", kisAppKey)
                .defaultHeader("appsecret", kisAppSecret)
                .build();
    }

    @Bean(name="kisAuthClient")
    public WebClient kisAuthClient(){
        return WebClient.builder()
                .baseUrl(kisEndPoint)
                .build();
    }

    @Bean(name="xmlClient")
    public WebClient xmlClient() {
        return WebClient.builder()
                .baseUrl(dataPortalEndPoint)
                .defaultHeader("dataPortalApiKey", dataPortalEndPoint)
                .defaultHeader("dataPortalSecret", dataPortalSecret)
                .build();
    }

    @Bean(name="keximClient")
    public WebClient keximClient() {
        return WebClient.builder()
                .baseUrl(keximEndpoint)
                .build();
    }

    @Bean(name="openPortalClient")
    public WebClient openPortalClient() {
        return WebClient.builder()
                .baseUrl(openPortalEndpoint)
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