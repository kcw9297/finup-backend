package app.finup.layer.base.validation.validator;

import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.Email;
import app.finup.layer.base.validation.annotation.YouTubeUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;

import java.util.Objects;

/**
 * 유튜브 URL 검증을 위한 클래스
 * @author kcw
 * @since 2025-12-12
 */

public class YouTubeUrlValidator implements ConstraintValidator<YouTubeUrl, String> {

    // 사용자 오류 메세지
    private String message;

    @Override
    public void initialize(YouTubeUrl annotation) {

        // 사용자 입력 오류 메세지
        message = annotation.message();
        this.message = Objects.isNull(message) || message.isBlank() ?
                "올바른 형식의 유튜브 URL을 입력해 주세요." : message;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        /*
            유튜브 영상 URL 양식은 아래 6가지 형태로 입력될 수 있음
            1. https://www.youtube.com/watch?v=dFcDgrYuA9A
            2. https://www.youtube.com/watch?v=dFcDgrYuA9A&t=379s
            3. https://youtu.be/dFcDgrYuA9A
            4. https://youtu.be/dFcDgrYuA9A?si=EFz5teqZUaoOmD0z
            5. https://m.youtube.com/watch?v=dFcDgrYuA9A
            6. https://www.youtube.com/embed/dFcDgrYuA9A
         */

        // [1] 검증 메세지 & 패턴
        String pattern = "^(https?://)?(www\\\\.|m\\\\.)?(youtube\\\\.com/(watch\\\\?v=|embed/)|youtu\\\\.be/)[a-zA-Z0-9_-]{11}.*$";

        // [2] 검증 수행
        if (Objects.isNull(value) || !value.matches(pattern)) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }
}
