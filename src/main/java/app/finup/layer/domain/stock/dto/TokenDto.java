package app.finup.layer.domain.stock.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * 토큰 DTO 클래스
 * @author lky
 * @since 2025-12-10
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class TokenDto {

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Token {

        @JsonProperty("access_token")
        private String accessToken; //접근토큰

        @JsonProperty("token_type")
        private String tokenType;   //접근토큰유형

        @JsonProperty("expires_in")
        private int expiresIn;      //접근토큰 유효기간
    }
}
