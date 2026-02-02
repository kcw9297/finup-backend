package app.finup.layer.base.validation.validator;

import app.finup.common.utils.TimeUtils;
import app.finup.layer.base.utils.ValidationUtils;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import app.finup.layer.base.validation.annotation.IntRange;

import java.util.Objects;

/**
 * int 타입 범위 검증을 위한 클래스
 * @author kcw
 * @since 2025-11-26
 */

public class IntRangeValidator implements ConstraintValidator<IntRange, Integer> {

    // 사용자 오류 메세지
    private String message;
    private int min;
    private int max;
    private boolean nullable;

    @Override
    public void initialize(IntRange annotation) {

        // 기본 파라미터
        min = Math.max(annotation.min(), 0);
        max = Math.max(annotation.max(), 1);
        nullable = annotation.nullable();

        // 숫자 포메팅
        String minStr = TimeUtils.formatKoreaNumber(min);
        String maxStr = TimeUtils.formatKoreaNumber(max);

        // 사용자 입력 오류 메세지
        message = annotation.message();
        if (Objects.isNull(message) || message.isBlank()) {
            if (min == 0) message = "최대 %s 이내의 양수를 입력해야 합니다.".formatted(maxStr);
            else message = "%s-%s 이내의 양수를 입력해야 합니다.".formatted(minStr, maxStr);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && Objects.isNull(value)) return true;

        // 범위 검증
        if (Objects.isNull(value) || value < min || value > max) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }

}