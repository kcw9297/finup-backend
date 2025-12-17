package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.PartSpecialTextValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 특수문자를 일부만 허용 문자열 검증을 적용할 어노테이션
 * @author kcw
 * @since 2025-12-17
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = PartSpecialTextValidator.class)  // Validator 클래스 지정
@Documented
public @interface PartSpecialText {
    String message() default "";
    int min() default 0;
    int max() default 255;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
