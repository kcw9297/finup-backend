package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.NoSpecialTextValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 특수문자를 허용하지 않는 문자열 검증을 적용할 애노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = NoSpecialTextValidator.class)  // Validator 클래스 지정
@Documented
public @interface NoSpecialText {
    String message() default "";
    int min() default 0;
    int max() default 255;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
