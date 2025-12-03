package app.finup.infra.jwt.manager;

import app.finup.common.constant.Const;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.JwtVerifyException;
import app.finup.infra.jwt.dto.JwtClaims;
import app.finup.infra.jwt.utils.JwtUtils;
import app.finup.infra.jwt.redis.RedisJwtStorage;
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

/**
 * JwtManager 구현 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
@Component
@RequiredArgsConstructor
public class JwtProviderImpl implements JwtProvider {

    private final RedisJwtStorage redisJwtStorage;

    @Value("${jwt.expiration.access-token}")
    public Duration expirationAt;

    @Value("${jwt.expiration.refresh-token}")
    public Duration expirationRt;

    @Override
    public String login(CustomUserDetails userDetails) {

        // [1] 토큰에 담길 Claims 생성
        Map<String, Object> claims = JwtUtils.setClaims(userDetails);
        String jti = (String) claims.get(Const.JTI);
        log.info("jti: {}, claims: {}", jti, claims);

        // [2] 토큰 생성
        Instant now = Instant.now(); // 현재 시간
        String rt = JwtUtils.generateToken(claims, Date.from(now.plus(expirationRt)));
        String at = JwtUtils.generateToken(claims, Date.from(now.plus(expirationAt)));

        // [3] RT Redis 내 저장
        redisJwtStorage.save(jti, rt);

        // [4] 로그인 처리 완료 후, 쿠키에 담을 AT 반환
        return at;
    }


    @Override
    public String reissue(String at) {

        // [1] 만료된 AT로 부터 jti 조회
        String jti = JwtUtils.getExpiredJti(at);

        // [2] jti 기반 Redis 내 RT 조회
        String rt = redisJwtStorage.get(jti);

        // [3] RT 검증 수행
        if (Objects.isNull(rt)) throw new JwtVerifyException(AppStatus.TOKEN_EXPIRED); // 조회 결과가 null 이면 만료된 인증
        JwtUtils.verify(rt); // 토큰 정보가 유효한지 검증

        // [4] 재발급 수행 및 반환
        return JwtUtils.reissueToken(rt, Date.from(Instant.now().plus(expirationAt)));
    }


    @Override
    public JwtClaims getClaims(String at) {
        return JwtUtils.verifyAndGetClaims(at);
    }


    @Override
    public void logout(String at) {

        // [1] AT 내 jti 조회
        String jti = JwtUtils.verifyAndGetJti(at);

        // [2] Redis 내 RT 무효화
        redisJwtStorage.delete(jti);
    }

}

