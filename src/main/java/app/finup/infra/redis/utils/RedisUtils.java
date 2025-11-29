package app.finup.infra.redis.utils;

import app.finup.common.constant.Const;
import lombok.AccessLevel;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
public class RedisUtils {

    /**
     * jwt redis key 생성
     * @param jti JWT jti
     * @return jti redis key
     */
    public static String createJwtKey(String jti) {
        return "%s%s".formatted(Const.PREFIX_KEY_JWT, jti);
    }
}
