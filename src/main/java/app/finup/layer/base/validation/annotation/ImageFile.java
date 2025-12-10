package app.finup.layer.base.validation.annotation;

import app.finup.layer.base.validation.validator.ImageFileValidator;
import jakarta.validation.Constraint;
import jakarta.validation.Payload;

import java.lang.annotation.*;

/**
 * 이미지 파일 검증을 위한 어노테이션
 * @author kcw
 * @since 2025-12-09
 */

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.PARAMETER})
@Constraint(validatedBy = ImageFileValidator.class)  // Validator 클래스 지정
@Documented
public @interface ImageFile {
    long maxSizeMB() default 10;
    boolean nullable() default false;
    Class<?>[] groups() default {};
    Class<? extends Payload>[] payload() default {};
}
