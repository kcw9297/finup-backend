package app.finup.security.jwt.generator;

import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.JwtVerifyException;
import app.finup.common.utils.LogUtils;
import app.finup.common.utils.StrUtils;
import app.finup.security.dto.CustomUserDetails;
import app.finup.security.dto.JwtClaims;
import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JwtGenerator 구현 클래스
 * @author kcw
 * @since 2026-01-23
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtGeneratorImpl implements JwtGenerator {

    // BASE64 암호화 키
    @Value("${jwt.secret-key}")
    private String secretKey;

    // SHA 암호화 적용 키
    private Key shaKey;

    // 초기화 로직
    @PostConstruct
    public void init() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        shaKey = Keys.hmacShaKeyFor(keyBytes); // SHA 암호화 적용
    }


    @Override
    public String generateToken(Map<String, Object> claims, Date expiration) {

        return Jwts.builder()
                .setClaims(claims) // 쿠키에 담길 claims 정보 (상세 정보)
                .setIssuedAt(Date.from(Instant.now())) // 발급일
                .setExpiration(expiration) // 만료 시간
                .signWith(shaKey) // 인증을 위한 SecretKey
                .compact();
    }


    @Override
    public String reissueToken(String rt, Date expiration) {

        // [1] RT 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(rt, false);

        // [2] AT 재발급 수행
        return generateToken(claims, expiration);
    }


    @Override
    public String verifyAndGetJti(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(token, false);

        // [2] 토큰 내 회원번호 반환
        return claims.get(Const.JTI, String.class);
    }


    @Override
    public JwtClaims verifyAndGetClaims(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(token, false);

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


    @Override
    public String verifyAndGetExpiredJti(String token) {

        // [1] 토큰 검증 후, 토큰 내 claims 조회
        Claims claims = getClaims(token, true);

        // [2] 토큰 내 회원번호 반환
        return claims.get(Const.JTI, String.class);
    }


    // Claims 조회 (검증 포함)
    private Claims getClaims(String token, boolean allowExpired) {

        try {
            // [1] 헤더에서 받아온 경우를 고려하여, Prefix 제거
            String jwt = token.replace(Const.PREFIX_BEARER, "");

            // [2] 토큰 파싱 및 반환 (예외 처리는 외부에서 수행)
            return Jwts.parserBuilder()
                    .setSigningKey(shaKey)
                    .build()
                    .parseClaimsJws(jwt)
                    .getBody();

        } catch (ExpiredJwtException e) {
            if (allowExpired) return e.getClaims(); // 만료를 허용하는 경우 Claims 반환
            LogUtils.showWarn(this.getClass(), "JWT 토큰 만료 : %s", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_EXPIRED, e);

        } catch (SecurityException e) {
            LogUtils.showError(this.getClass(), "JWT 서명 검증 실패 : %s", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (MalformedJwtException e) {
            LogUtils.showError(this.getClass(), "JWT 토큰 형식 오류 : %s", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (UnsupportedJwtException e) {
            LogUtils.showError(this.getClass(), "지원하지 않는 토큰 : %s", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_INVALID, e);

        } catch (IllegalArgumentException e) {
            LogUtils.showError(this.getClass(), "인증 토큰 없음: %s", e.getMessage());
            throw new JwtVerifyException(AppStatus.TOKEN_NOT_FOUND, e);
        }
    }
}
