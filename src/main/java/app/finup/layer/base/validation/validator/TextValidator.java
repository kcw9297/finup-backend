package app.finup.layer.base.validation.validator;

import app.finup.layer.base.validation.annotation.Text;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 특수문자와 무관하게, 문자열 길이만을 검증할 클래스
 * @author kcw
 * @since 2025-11-2697
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
        this.message = annotation.message();
        this.min = Math.max(annotation.min(), 0);
        this.max = Math.max(annotation.max(), 0);
        this.nullable = annotation.nullable();

        // 사용자 입력 오류 메세지
        if (Objects.isNull(message) || message.isBlank()) {
            if (Objects.equals(min, 0)) this.message = "최대 %d자 이내 내용을 입력해야 합니다.".formatted(max);
            else this.message = "%d-%d자 사이 이내 내용을 입력해야 합니다.".formatted(min, max);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // [1] 패턴
        String pattern = "^[가-힣a-zA-Z0-9\\s\\r\\n]{%d,%d}$".formatted(min, max);

        // [2] 기본 메세지 비활성화
        context.disableDefaultConstraintViolation();

        // [3] 검증 수행
        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && Objects.isNull(value)) return true;

        // 패턴 검증
        if (Objects.isNull(value) || !value.matches(pattern)) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        return true;
    }
}
