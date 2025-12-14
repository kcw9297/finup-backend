package app.finup.layer.base.validation.validator;

import app.finup.common.utils.StrUtils;
import app.finup.infra.youtube.utils.YouTubeUtils;
import app.finup.layer.base.utils.ValidationUtils;
import app.finup.layer.base.validation.annotation.YouTubeUrl;
import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import lombok.extern.slf4j.Slf4j;

import java.util.Objects;

/**
 * 유튜브 URL 검증을 위한 클래스
 * @author kcw
 * @since 2025-12-12
 */

@Slf4j
public class YouTubeUrlValidator implements ConstraintValidator<YouTubeUrl, String> {

    // 사용자 오류 메세지
    private String message;

    @Override
    public void initialize(YouTubeUrl annotation) {

        // 사용자 입력 오류 메세지
        message = annotation.message();
        this.message = Objects.isNull(message) || message.isBlank() ?
                "올바른 유튜브 URL을 입력해 주세요.\nex. https://www.youtube.com/watch?v=17fQ_XxoBLs" : message;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        if (Objects.isNull(value) || !value.matches(YouTubeUtils.PATTERN_VIDEO_URL)) {
            ValidationUtils.addViolation(context, message);
            return false;
        }

        return true;
    }
}
