package app.finup.layer.base.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import app.finup.layer.base.validation.validator.LongRangeValidator;

import java.lang.annotation.*;

/**
 * long 타입 범위 검증을 적용할 어노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = LongRangeValidator.class)
@Documented
public @interface LongRange {

    String message() default "";
    long min() default 0L; // 양수 범위만
    long max() default Long.MAX_VALUE;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
