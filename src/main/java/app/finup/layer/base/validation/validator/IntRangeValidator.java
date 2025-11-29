package app.finup.layer.base.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import app.finup.common.utils.StrUtils;
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
        this.min = Math.max(annotation.min(), 0);
        this.max = Math.max(annotation.max(), 0);
        this.nullable = annotation.nullable();

        // 숫자 포메팅
        String minStr = StrUtils.formatKoreaNumber(min);
        String maxStr = StrUtils.formatKoreaNumber(max);

        // 사용자 입력 오류 메세지
        this.message = annotation.message();
        if (Objects.isNull(message) || message.isBlank()) {
            if (Objects.equals(min, 0)) this.message = "최대 %s 이내의 양수를 입력해야 합니다.".formatted(maxStr);
            else this.message = "%s-%s 이내의 양수를 입력해야 합니다.".formatted(minStr, maxStr);
        }
    }

    @Override
    public boolean isValid(Integer value, ConstraintValidatorContext context) {

        // [1] 기본 메세지 비활성화
        context.disableDefaultConstraintViolation();

        // [2] 검증 수행
        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && Objects.isNull(value)) return true;

        // 범위 검증
        if (value < min || value > max) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }

}