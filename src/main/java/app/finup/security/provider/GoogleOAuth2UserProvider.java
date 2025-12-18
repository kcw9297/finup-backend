package app.finup.security.provider;

import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ProviderException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.layer.domain.member.enums.MemberSocial;
import app.finup.security.dto.OAuth2UserInfo;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.reactive.function.BodyInserters;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

import java.util.Map;

@Slf4j
@Component
@RequiredArgsConstructor
public class GoogleOAuth2UserProvider implements OAuth2UserProvider {

    // 사용 의존성
    private final WebClient oauth2GoogleClient;

    // 사용 상수
    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String clientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String clientSecret;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String redirectUri;

    private static final String TOKEN_URI = "https://oauth2.googleapis.com/token";
    private static final String USER_INFO_URI = "https://www.googleapis.com/oauth2/v3/userinfo";


    @Override
    public String getAccessToken(String code) {

        // AT 조회 요청
        return oauth2GoogleClient.post() // POST METHOD
                .uri(TOKEN_URI)
                .contentType(MediaType.APPLICATION_FORM_URLENCODED)
                .body(BodyInserters.fromFormData(createFormData(code)))
                .retrieve() // 요청 수행
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    LogUtils.showError(this.getClass(), "구글 토큰 요청 실패 : %s", errorBody);
                                    return Mono.error(new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED));
                                })
                )
                .bodyToMono(new ParameterizedTypeReference<Map<String, Object>>() {}) // JSON 역직렬화
                .switchIfEmpty(Mono.error(() -> {
                    LogUtils.showError(this.getClass(), "OAuth2 토큰 응답이 비어있습니다");
                    return new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED);
                }))
                .map(map -> map.get("access_token"))
                .switchIfEmpty(Mono.error(() -> {
                    LogUtils.showError(this.getClass(), "OAuth2 토큰 내 AccessToken이 존재하지 않습니다.");
                    return new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED);
                }))
                .map(obj -> (String) obj)
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof ProviderException) return ex;
                    LogUtils.showError(this.getClass(), "OAuth2 소셜 로그인 시도를 위한 AccessToken 획득 실패.\n원인 : %s", ex.getMessage());
                    return new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED, ex);
                })
                .block();  // 동기식으로 변환
    }

    // 소셜 로그인 요청 Form
    private MultiValueMap<String, String> createFormData(String code) {

        MultiValueMap<String, String> formData = new LinkedMultiValueMap<>();
        formData.add("code", code);
        formData.add("client_id", clientId);
        formData.add("client_secret", clientSecret);
        formData.add("redirect_uri", redirectUri);
        formData.add("grant_type", "authorization_code");
        return formData;
    }



    @Override
    public OAuth2UserInfo getOAuth2UserInfo(String accessToken) {

        // [1] 구글 소셜회원정보 조회
        GoogleUserResponse response = oauth2GoogleClient.get() // GET METHOD
                .uri(USER_INFO_URI)
                .header("Authorization", "Bearer " + accessToken) // AT 기반 소셜 로그인 요청
                .retrieve() // 요청 전송
                .onStatus(
                        status -> status.is4xxClientError() || status.is5xxServerError(),
                        clientResponse -> clientResponse.bodyToMono(String.class)
                                .flatMap(errorBody -> {
                                    LogUtils.showError(this.getClass(), "Google 소셜회원 조회 요청 실패! : %s", errorBody);
                                    return Mono.error(new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED));
                                })
                )
                .bodyToMono(String.class)
                .switchIfEmpty(Mono.error(() -> {
                    LogUtils.showError(this.getClass(), "Google 소셜회원 조회 결과가 올바르지 않습니다!");
                    return new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED);
                }))
                .map(json -> StrUtils.fromJson(json, GoogleUserResponse.class))
                .onErrorMap(Exception.class, ex -> {
                    if (ex instanceof ProviderException) return ex;
                    LogUtils.showError(this.getClass(), "OAuth2 소셜 로그인 실패.\n원인 : %s", ex.getMessage());
                    return new ProviderException(AppStatus.AUTH_OAUTH2_LOGIN_FAILED, ex);
                })
                .block();// 동기식으로 변환

        // [2] 조회 정보 기반 DTO 매핑 및 반환
        return OAuth2UserInfo.builder()
                .providerId(response.getSub())
                .email(response.getEmail())
                .nickname(StrUtils.createRandomNumString(10, MemberSocial.GOOGLE.name()))
                .social(MemberSocial.GOOGLE)
                .build();

    }


    // Google 전용 응답 DTO
    @Getter
    @NoArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    private static class GoogleUserResponse {
        private String sub;
        private String email;
        private String name;
        private String picture;

        @JsonProperty("email_verified")
        private Boolean emailVerified;
    }

}
