package app.finup.infra.aop;

import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;

/**
 * Spring AOP 적용을 위한 Pointcut 식을 관리하는 클래스
 * @author kcw
 * @since 2025-11-28
 */
@Aspect
class Pointcuts {

    /*
     * Pointcut - package 범위
     * 보안, 로그 관련영역은 범위에서 제외
     */

    // security package
    @Pointcut("within(app.finup.security..*)")
    public void securityPack() {}

    // security.filter package
    @Pointcut("within(app.finup.security.filter..*)")
    public void filterPack() {}

    // security.filter 제외 한 모든 패키지
    @Pointcut("within(app.finup..*) && !filterPack()")
    public void allPack() {}

    // security 패키지를 제외한 모든 패키지
    @Pointcut("within(app.finup..*) && !securityPack()")
    public void basePack() {}

    // app.finup.layer 내 패키지
    @Pointcut("within(app.finup.layer..*) && !securityPack()")
    public void layerPack() {}

    // app.finup.layer.domain 내 패키지
    @Pointcut("within(app.finup.layer.domain..*) && !securityPack()")
    public void domainPack() {}

    /*
     * Pointcut - Annotation 적용 클래스
     */

    // @RestController 적용 클래스
    @Pointcut("@within(org.springframework.web.bind.annotation.RestController) && basePack()")
    public void restCtlAnno() {}
}