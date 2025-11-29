package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.TextValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 특수문자와 무관하게, 문자열 길이만을 검증을 위한 애노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = TextValidator.class)  // Validator 클래스 지정
@Documented
public @interface Text {
    String message() default "";
    int min() default 0;
    int max() default 255;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
