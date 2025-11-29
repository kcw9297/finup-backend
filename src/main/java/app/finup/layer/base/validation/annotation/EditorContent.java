package app.finup.layer.base.validation.annotation;

import jakarta.validation.Constraint;
import jakarta.validation.Payload;
import app.finup.layer.base.validation.validator.EditorContentValidator;

import java.lang.annotation.*;

/**
 * 에디터 작성 내용 검증을 적용할 애노테이션
 * @author kcw
 * @since 2025-11-26
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = EditorContentValidator.class)  // Validator 클래스 지정
@Documented
public @interface EditorContent {
    String message() default "";
    int min() default 1;
    int max() default 2000;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
