package app.finup.common.exception;

import app.finup.common.enums.AppStatus;
import lombok.Getter;

import java.util.Map;

/**
 * 유효성 검사 예외 (= Bean Validation)
 * @author kcw
 * @since 2025-11-26
 */

@Getter
public class ValidationException extends AppException {

    public ValidationException(Map<String, String> errors) {
        super(AppStatus.VALIDATION_INVALID_PARAMETER, errors);
    }
}
