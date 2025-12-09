package app.finup.layer.base.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import app.finup.layer.base.validation.validator.NicknameValidator;

import java.lang.annotation.*;

/**
 * 닉네임 검증을 적용할 어노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = NicknameValidator.class)  // Validator 클래스 지정
@Documented
public @interface Nickname {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
