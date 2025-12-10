package app.finup.layer.base.validation.validator;

import app.finup.layer.base.utils.ValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import app.finup.layer.base.validation.annotation.Nickname;

import java.util.Objects;

/**
 * 닉네임 검증을 위한 검증 클래스
 * @author kcw
 * @since 2025-11-26
 */

public class NicknameValidator implements ConstraintValidator<Nickname, String> {

    // 사용자 오류 메세지
    private String message;

    @Override
    public void initialize(Nickname annotation) {

        // 사용자 입력 오류 메세지
        message = annotation.message();
        this.message = Objects.isNull(message) || message.isBlank() ?
                "3-12자 사이 한글/영문/숫자를 입력해야 합니다." : message;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // [1] 패턴
        String pattern = "^[가-힣a-zA-Z0-9]{3,12}$";

        // [2] 검증 수행
        if (Objects.isNull(value) || value.isBlank() || !value.matches(pattern)) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }
}
