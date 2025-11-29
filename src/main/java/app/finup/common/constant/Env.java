package app.finup.common.constant;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

/**
 * Spring Boot Application Profile 상수 관리
 * @author kcw
 * @since 2025-11-26
 */
@NoArgsConstructor(access = AccessLevel.PRIVATE)
public final class Env {

    public static final String PROFILE_LOCAL = "local";    // 로컬 개발용
    public static final String PROFILE_PROD = "prod";      // 실제 배포용

    public static final String DDL_AUTO_CREATE = "create";


    // 환경 변수 경로
    public static final String PROP_DDL_AUTO = "spring.jpa.hibernate.ddl-auto";
}
