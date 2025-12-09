package app.finup.layer.base.validation.validator;

import app.finup.layer.base.validation.annotation.Select;
import app.finup.layer.base.validation.annotation.Text;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * Select 선택 값 검증 클래스
 * @author kcw
 * @since 2025-12-09
 */

@Slf4j
public class SelectValidator implements ConstraintValidator<Select, Object> {

    // 사용자 오류 메세지
    private String message;

    @Override
    public void initialize(Select annotation) {

        // 기본 파라미터
        this.message = annotation.message();

        // 사용자 입력 오류 메세지
        if (Objects.isNull(message) || message.isBlank())
            this.message = "값을 선택해야 합니다";

    }

    @Override
    public boolean isValid(Object value, ConstraintValidatorContext context) {

        // [1] 기본 메세지 비활성화
        context.disableDefaultConstraintViolation();

        // [2] 검증 수행
        if (Objects.isNull(value)) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
