package app.finup.layer.base.validation.validator;

import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.NoSpecialText;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 특수문자를 허용하지 않는 문자열 검증을 위한 클래스
 * <br>게시글 제목, 공지사항 제목 등 제목과 관련한 곳에 이용
 * @author kcw
 * @since 2025-11-26
 */

@Slf4j
public class NoSpecialTextValidator implements ConstraintValidator<NoSpecialText, String> {

    // 사용자 오류 메세지
    private String message;
    private int min;
    private int max;
    private boolean nullable;

    @Override
    public void initialize(NoSpecialText annotation) {

        // 기본 파라미터
        message = annotation.message();
        min = Math.max(annotation.min(), 1);
        max = Math.max(annotation.max(), 2);
        nullable = annotation.nullable();

        // 사용자 입력 오류 메세지
        if (Objects.isNull(message) || message.isBlank()) {
            if (min <= 1) message = "최대 %d자 이내 한글/영문/숫자를 입력해야 합니다.".formatted(max);
            else message = "%d-%d자 사이 한글/영문/숫자를 입력해야 합니다.".formatted(min, max);
        }
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // [1] 패턴
        String pattern = "^[가-힣a-zA-Z0-9\\s]{%d,%d}$".formatted(min, max);

        // [2] 검증 수행
        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && Objects.isNull(value)) return true;

        // 패턴 검증
        if (Objects.isNull(value) || !value.matches(pattern)) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }
}
