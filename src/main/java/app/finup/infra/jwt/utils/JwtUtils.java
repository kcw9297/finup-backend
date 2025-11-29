package app.finup.infra.jwt.utils;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.JwtVerifyException;
import app.finup.common.utils.StrUtils;
import app.finup.infra.jwt.dto.JwtClaims;
import app.finup.security.dto.CustomUserDetails;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JWT 인증과 관련한 유틸 함수 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class JwtUtils {

    // Secret Key (SHA-256 알고리즘)
    private static final Key SECRET_KEY = Keys.secretKeyFor(SignatureAlgorithm.HS256);

    // Authorization Prefix
    private static final String TOKEN_PREFIX = "Bearer ";


    public static Map<String, Object> setClaims(CustomUserDetails userDetails) {

        // [1] UUID 생성 (JWT ID, JWT 토큰을 고유하게 식별하는 값)
        String jti = StrUtils.createUUID();

        // [2] claims 내 필요 정보 세팅 후 반환
        Map<String, Object> claims = new ConcurrentHashMap<>();
        claims.put(Const.JTI, jti);
        claims.put(Const.MEMBER_ID, userDetails.getMemberId());
        claims.put(Const.EMAIL, userDetails.getEmail());
        claims.put(Const.NICKNAME, userDetails.getNickname());
        claims.put(Const.IS_ACTIVE, userDetails.getIsActive());
        claims.put(Const.ROLE, userDetails.getRole());
        claims.put(Const.SOCIAL, userDetails.getSocial());
        return claims;
    }

    public static String generateToken(Map<String, Object> claims, Date expiration) {

        return Jwts.builder()
                .setClaims(claims) // 쿠키에 담길 claims 정보 (상세 정보)
                .setIssuedAt(Date.from(Instant.now())) // 발급일
                .setExpiration(expiration) // 만료 시간
                .signWith(SECRET_KEY) // 인증을 위한 SecretKey
                .compact();
    }


    public static String reissueToken(String rt, Date expiration) {

        // [1] RT 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(rt);

        // [2] AT 재발급 수행
        return generateToken(claims, expiration);
    }


    public static Long verifyAndGetMemberId(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(token);

        // [2] 토큰 내 회원번호 반환
        return claims.get(Const.MEMBER_ID, Long.class);
    }

    public static String verifyAndGetJti(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(token);

        // [2] 토큰 내 회원번호 반환
        return claims.get(Const.JTI, String.class);
    }

    public static void verify(String token) {
        getClaims(token);
    }


    public static JwtClaims verifyAndGetClaims(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(token);

        // [2] Claims 데이터를 DTO 매핑 및 반환
        return JwtClaims.builder()
                .jti(claims.get(Const.JTI, String.class))
                .memberId(claims.get(Const.MEMBER_ID, Long.class))
                .email(claims.get(Const.EMAIL, String.class))
                .nickname( claims.get(Const.NICKNAME, String.class))
                .role(claims.get(Const.ROLE, String.class))
                .social(claims.get(Const.SOCIAL, String.class))
                .build();
    }

    public static String getExpiredJti(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getExipredClaims(token);

        // [2] 토큰 내 회원번호 반환
        return claims.get(Const.JTI, String.class);
    }


    // Claims 조회 (만료, 서명오류 등으로 조회 불가능 시 예외 발생)
    private static Claims getClaims(String token) {

        try {
            // [1] 헤더에서 받아온 경우를 고려하여, Prefix 제거
            String jwt = token.replace(TOKEN_PREFIX, "");

            // [2] 토큰 파싱 및 반환 (예외 처리는 외부에서 수행)
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

        } catch (ExpiredJwtException e) {
            log.warn("토큰 만료: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_EXPIRED, e);

        } catch (SecurityException e) {
            log.error("서명 검증 실패: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (MalformedJwtException e) {
            log.error("잘못된 토큰 형식: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (IllegalArgumentException e) {
            log.error("인증 토큰 없음: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_NOT_FOUND, e);
        }
    }


    // 만료된 AT로부터 Claims 조회
    private static Claims getExipredClaims(String token) {

        try {
            // [1] 헤더에서 받아온 경우를 고려하여, Prefix 제거
            String jwt = token.replace(TOKEN_PREFIX, "");

            // [2] 토큰 파싱 및 반환 (예외 처리는 외부에서 수행)
            return Jwts.parserBuilder()
                    .setSigningKey(SECRET_KEY)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

        } catch (ExpiredJwtException e) {
            return e.getClaims();

        } catch (SecurityException e) {
            log.error("서명 검증 실패: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (MalformedJwtException e) {
            log.error("잘못된 토큰 형식: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (UnsupportedJwtException e) {
            log.error("지원하지 않는 토큰: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (IllegalArgumentException e) {
            log.error("인증 토큰 없음: {}", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_NOT_FOUND, e);
        }
    }
}
