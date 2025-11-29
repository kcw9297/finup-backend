package app.finup.layer.base.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import app.finup.layer.base.validation.validator.IntRangeValidator;

import java.lang.annotation.*;

/**
 * int 타입 범위 검증을 적용할 애노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = IntRangeValidator.class)
@Documented
public @interface IntRange {

    String message() default "";
    int min() default 0;  // 양수 범위만
    int max() default Integer.MAX_VALUE;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
