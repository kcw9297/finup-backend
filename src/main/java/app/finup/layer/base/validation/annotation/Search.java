package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.SearchValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 검색어 검증 애노테이션
 * @author kcw
 * @since 2026-01-22
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = SearchValidator.class)  // Validator 클래스 지정
@Documented
public @interface Search {
    String message() default "";
    int min() default 0;
    int max() default 255;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
