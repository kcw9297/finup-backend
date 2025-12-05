package app.finup.common.utils;

import app.finup.common.constant.Env;
import org.springframework.core.env.Environment;

import java.util.Arrays;
import java.util.Objects;

/**
 * 환경 변수와 관련한 유틸 함수 클래스
 * @author kcw
 * @since 2025-11-26
 */

public final class EnvUtils {

    public static boolean hasProfile(Environment env, String profile) {
        return Arrays.asList(env.getActiveProfiles()).contains(profile);
    }

    public static boolean isLocal(Environment env) {
        return Arrays.asList(env.getActiveProfiles()).contains(Env.PROFILE_LOCAL);
    }

    public static boolean isProd(Environment env) {
        return Arrays.asList(env.getActiveProfiles()).contains(Env.PROFILE_PROD);
    }

    public static boolean isPropertyEquals(Environment env, String property, String value) {

        // [1] 프로퍼티 조회
        String prop = env.getProperty(property);

        // [2] 프로퍼티 검증 후 반환
        return Objects.nonNull(prop) && Objects.equals(prop, value);
    }
}

