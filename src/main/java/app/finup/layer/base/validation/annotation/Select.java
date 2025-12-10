package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.SelectValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * Select 선택 값 검증을 위한 어노테이션
 * @author kcw
 * @since 2025-12-09
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = SelectValidator.class)  // Validator 클래스 지정
@Documented
public @interface Select {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
