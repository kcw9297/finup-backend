package app.finup.security.jwt.generator;

import app.finup.security.dto.JwtClaims;

import java.util.Date;
import java.util.Map;

/**
 * JWT 토큰 생성 기능 제공 인터페이스 (기존 JwtUtils 클래스를 빈으로 변경한 형태)
 * @author kcw
 * @since 2026-01-23
 */
public interface JwtGenerator {

    /**
     * JWT 토큰 발급(생성)
     * @param claims JWT Claims 정보 객체 (Map<String, Object> 구현체)
     * @param expiration 만료 시점
     * @return 생성된 JWT 토큰 문자열
     */
    String generateToken(Map<String, Object> claims, Date expiration);


    /**
     * JWT 토큰 재발급
     * @param expiration 만료 시점
     * @return 생성된 JWT 문자열
     */
    String reissueToken(String rt, Date expiration);


    /**
     * JWT 토큰 검증 후 Claim 내 JTI 고유 값 조회 (검증 실패 시 예외 반환)
     * @param token JWT AT(AccessToken)
     * @return JTI 문자열
     */
    String verifyAndGetJti(String token);


    /**
     * JWT 토큰 검증 후 JWT Claim 조회 (검증 실패 시 예외 반환)
     * @param token JWT AT(AccessToken)
     */
    JwtClaims verifyAndGetClaims(String token);


    /**
     * 만료된 JWT 토큰 내 JTI 고유 값 조회 (검증 실패 시 예외 반환)
     * @param token JWT AT(AccessToken)
     */
    String verifyAndGetExpiredJti(String token);
}
