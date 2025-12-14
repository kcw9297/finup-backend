package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.YouTubeUrlValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 유튜브 URL 검증을 적용할 어노테이션
 * @author kcw
 * @since 2025-12-12
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = YouTubeUrlValidator.class)  // Validator 클래스 지정
@Documented
public @interface YouTubeUrl {
    String message() default "";
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
