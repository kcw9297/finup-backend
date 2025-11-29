package app.finup.layer.base.validation.validator;

import jakarta.validation.ConstraintValidator;
import jakarta.validation.ConstraintValidatorContext;
import app.finup.layer.base.validation.annotation.Email;

import java.util.Objects;

/**
 * 이메일 검증을 위한 검증 클래스
 * @author kcw
 * @since 2025-11-26
 */

public class EmailValidator implements ConstraintValidator<Email, String> {

    // 사용자 오류 메세지
    private String message;

    @Override
    public void initialize(Email annotation) {

        // 사용자 입력 오류 메세지
        String message = annotation.message();
        this.message = Objects.isNull(message) || message.isBlank() ?
                "30자 이내 이메일 형식을 입력해야 합니다." : message;
    }

    @Override
    public boolean isValid(String value, ConstraintValidatorContext context) {

        /*
            * RFC 5322 (이메일 형식 표준) 참고
            1. 1자 이상 30자 이하의 문자열
            2. 문자열 시작 : 특수문자로 시작 불가 (영어, 숫자만 사용)
            3. 문자열 앞 (로컬) : 영문 대소문자, 숫자, 마침표, 언더바, 하이픈 허용. 1개 이상 필수
            4. 로컬 뒤 @ 필수
            5. @ 뒤 도메인 : 영문 대소문자, 숫자, 마침표, 하이픈 허용
            6. 도메인 뒤 . 필수
            7. 마지막 도메인 : 영문자만 허용 (com, kr, net, ...)
         */


        // [1] 검증 메세지 & 패턴
        String pattern = "^(?=.{1,30}$)[a-zA-Z0-9][a-zA-Z0-9._-]*@[a-zA-Z0-9][a-zA-Z0-9.-]*\\.[a-zA-Z]{2,}$";

        // [2] 기본 메세지 비활성화
        context.disableDefaultConstraintViolation();

        // [3] 검증 수행
        if (Objects.isNull(value) || !value.matches(pattern)) {
            context.buildConstraintViolationWithTemplate(message).addConstraintViolation();
            return false;
        }

        // 검증 통과
        return true;
    }
}
