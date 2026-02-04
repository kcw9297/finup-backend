package app.finup.layer.base.validation.validator;

import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.Text;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 특수문자와 무관하게, 문자열 길이만을 검증할 클래스
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
public class TextValidator implements ConstraintValidator<Text, String> {

    // 사용자 오류 메세지
    private String message;
    private int min;
    private int max;
    private boolean nullable;

    @Override
    public void initialize(Text annotation) {

        // 기본 파라미터
        message = annotation.message();
        min = Math.max(annotation.min(), 1);
        max = Math.max(annotation.max(), 2);
        nullable = annotation.nullable();

        // 사용자 입력 오류 메세지
        if (Objects.isNull(message) || message.isBlank()) {
            if (min <= 1) message = "최대 %d자 이내 내용을 입력해야 합니다.".formatted(max);
            else message = "%d-%d자 사이 내용을 입력해야 합니다.".formatted(min, max);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && Objects.isNull(value)) return true;

        // 허용하지 않는 태그 여부 검증
        if (!ValidationUtils.isValidText(value)) {
            ValidationUtils.addViolation(context, "허용되지 않는 문자열이 존재합니다.");
            return false;
        }

        // 길이 검증
        String pureContext = ValidationUtils.removeHtmlTags(value); // HTML 태그 제거
        if (Objects.isNull(pureContext) || pureContext.length() < min || pureContext.length() > max) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }
}
