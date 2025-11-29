package app.finup.infra.jwt.manager;

import app.finup.infra.jwt.dto.JwtClaims;
import app.finup.security.dto.CustomUserDetails;

public interface JwtManager {

    /**
     * 로그인 - AccessToken, RefreshToken 발급
     * @param userDetails Spring Security CustomUserDetails
     * @return 발급받은 AccessToken 암호화 문자열
     */
    String login(CustomUserDetails userDetails);

    /**
     * 토큰 재발급 (AccessToken 만료 시)
     * @param at JWT AccessToken
     * @return 새롭게 발급받은 JWT AccessToken
     */
    String reissue(String at);

    /**
     * JWT 내 Claims 조회 (토큰 내 상세정보)
     * @param at JWT AccessToken
     * @return JWT Claims 정보를 담은 Custom DTO
     */
    JwtClaims getClaims(String at);

    /**
     * 로그아웃 - RT 무효화
     * @param at JWT AccessToken
     */
    void logout(String at);
}
