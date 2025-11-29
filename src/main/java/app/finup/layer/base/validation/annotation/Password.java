package app.finup.layer.base.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import app.finup.layer.base.validation.validator.PasswordValidator;

import java.lang.annotation.*;

/**
 * 비밀번호 검증을 적용할 애노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = PasswordValidator.class)  // Validator 클래스 지정
@Documented
public @interface Password {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
