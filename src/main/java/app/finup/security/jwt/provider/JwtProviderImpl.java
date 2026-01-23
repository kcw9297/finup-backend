package app.finup.security.jwt.provider;

import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.JwtVerifyException;
import app.finup.common.utils.StrUtils;
import app.finup.security.dto.JwtClaims;
import app.finup.security.jwt.generator.JwtGenerator;
import app.finup.security.jwt.redis.JwtRedisStorage;
import app.finup.security.dto.CustomUserDetails;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.Instant;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

/**
 * JwtManager 구현 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    // 사용 의존성
    private final JwtGenerator jwtGenerator;
    private final JwtRedisStorage jwtRedisStorage;

    @Value("${jwt.expiration.access-token}")
    public Duration expirationAt;

    @Value("${jwt.expiration.refresh-token}")
    public Duration expirationRt;

    @Override
    public String login(CustomUserDetails userDetails) {

        // [1] 토큰에 담길 Claims 생성
        Map<String, Object> claims = setClaims(userDetails);
        String jti = (String) claims.get(Const.JTI);

        // [2] 토큰 생성
        Instant now = Instant.now(); // 현재 시간
        String rt = jwtGenerator.generateToken(claims, Date.from(now.plus(expirationRt)));
        String at = jwtGenerator.generateToken(claims, Date.from(now.plus(expirationAt)));

        // [3] RT Redis 내 저장
        jwtRedisStorage.save(jti, rt);

        // [4] 로그인 처리 완료 후, 쿠키에 담을 AT 반환
        return at;
    }

    // JWT 토큰에 담을 Claims 생성
    public Map<String, Object> setClaims(CustomUserDetails userDetails) {

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


    @Override
    public String reissue(String at) {

        // [1] 만료된 AT로 부터 jti 조회
        String jti = jwtGenerator.verifyAndGetExpiredJti(at);

        // [2] jti 기반 Redis 내 RT 조회
        String rt = jwtRedisStorage.get(jti);
        log.warn("rt = {}", rt);

        // [3] RT 검증 후, 재발급 수행
        if (Objects.isNull(rt)) throw new JwtVerifyException(AppStatus.TOKEN_EXPIRED_RT); // 조회 결과가 null 이면 만료된 인증
        return jwtGenerator.reissueToken(rt, Date.from(Instant.now().plus(expirationAt)));
    }


    @Override
    public JwtClaims getClaims(String at) {

        // [1] claims 조회
        JwtClaims claims = jwtGenerator.verifyAndGetClaims(at);

        // [2] jti 기반 RT 조회
        String jti = claims.getJti();
        String rt = jwtRedisStorage.get(jti);

        // [3] 만약 rt가 존재하지 않는 경우, 예외 반환 (만료)
        if (Objects.isNull(rt))
            throw new JwtVerifyException(AppStatus.TOKEN_EXPIRED_RT); // 조회 결과가 null 이면 만료된 인증

        // [4] RT 정상 존재 시, claims 반환
        return claims;
    }


    @Override
    public void logout(String at) {

        // [1] AT 내 jti 조회 (만료되어도 예외가 발생하지 않도록 조회)
        String jti = jwtGenerator.verifyAndGetExpiredJti(at);

        // [2] Redis 내 RT 무효화
        jwtRedisStorage.delete(jti);
    }

}

