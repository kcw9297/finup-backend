package app.finup.layer.base.validation.validator;

import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.NoSpecialText;
import app.finup.layer.base.validation.annotation.PartSpecialText;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 특수문자를 일부만 허용 문자열 검증을 적용하는 검증 클래스
 * 허용 특수문자 : !@#$%^&*()-=_+[]{}/.,'"
 * @author kcw
 * @since 2025-12-17
 */

@Slf4j
public class PartSpecialTextValidator implements ConstraintValidator<PartSpecialText, String> {

    // 사용자 오류 메세지
    private String message;
    private int min;
    private int max;
    private boolean nullable;
    private final String ALLOWED_SPECIALS = "!@#$%^&*()\\-=_+\\[\\]{}/.,'\"";;

    @Override
    public void initialize(PartSpecialText annotation) {

        // 기본 파라미터
        message = annotation.message();
        min = Math.max(annotation.min(), 1);
        max = Math.max(annotation.max(), 2);
        nullable = annotation.nullable();

        // 사용자 입력 오류 메세지
        if (Objects.isNull(message) || message.isBlank()) {
            if (min <= 1) message = "최대 %d자 이내 한글/영문/숫자를 입력해야 합니다.\n허용 특수문자 %s: ".formatted(max, ALLOWED_SPECIALS);
            else message = "%d-%d자 사이 한글/영문/숫자를 입력해야 합니다.\n허용 특수문자 %s: ".formatted(min, max, ALLOWED_SPECIALS);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // [1] 패턴
        String pattern = "^[가-힣a-zA-Z0-9\\s%s]{%d,%d}$".formatted(ALLOWED_SPECIALS, min, max);
        String subPattern = ".*[가-힣a-zA-Z]+.*"; // 숫자, 특수문자만 있는 경우 제거

        // [2] 검증 수행
        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && Objects.isNull(value)) return true;

        // 패턴 검증
        if (Objects.isNull(value) || !value.matches(pattern) || !value.matches(subPattern)) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }
}
