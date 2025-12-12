package app.finup.infra.mail;

import java.time.Duration;

/**
 * 메일 발송 프로바이더
 *  - 인증 URL / 인증 코드 전송 담당
 */
public interface MailProvider {

    /**
     * 비밀번호 변경 등 URL 기반 인증 메일
     */
    void sendVerifyUrl(String to, String verifyUrl, Duration expiration);

    /**
     * 회원가입 등 숫자 코드 기반 인증 메일
     */
    void sendVerifyCode(String to, String verifyCode, Duration expiration);
}
