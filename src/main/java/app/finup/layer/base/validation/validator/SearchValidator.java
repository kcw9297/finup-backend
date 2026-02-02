package app.finup.layer.base.validation.validator;

import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.Search;
import app.finup.layer.base.validation.annotation.Text;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 검색어를 검증할 검증 클래스
 * @author kcw
 * @since 2026-01-22
 */

@Slf4j
public class SearchValidator implements ConstraintValidator<Search, String> {

    // 사용자 오류 메세지
    private int min;
    private int max;
    private boolean nullable;

    // 검증 패턴
    private static final String PATTERN = "^[가-힣a-zA-Z\\s]+$";

    @Override
    public void initialize(Search annotation) {

        // 기본 파라미터
        min = Math.max(annotation.min(), 1);
        max = Math.max(annotation.max(), 2);
        nullable = annotation.nullable();
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        // null이 가능한 경우, 입력이 null이면 통과
        if (nullable && (Objects.isNull(value) || value.isBlank())) return true;

        // 검색어 길이가 범위 내인지 검증
        if (!ValidationUtils.isValidText(value)) {
            ValidationUtils.addViolation(context, "유효한 검색어를 입력해 주세요.");
            return false;
        }

        // 길이 검증
        if (value.length() < min || value.length() > max) {
            String message = min > 1 ?
                    "%d~%d 자 사이의 검색어를 입력해 주세요.".formatted(min, max) :
                    "%d자 이내 검석어를 입력해 주세요".formatted(max);
            ValidationUtils.addViolation(context, message);
            return false;
        }

        // 패턴 검증
        if (!value.matches(PATTERN)) {
            ValidationUtils.addViolation(context, "검색어는 한글, 영어만 입력 가능합니다.");
            return false;
        }

        return true;
    }
}
