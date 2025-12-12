package app.finup.infra.mail;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.ClassPathResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Component;
import app.finup.common.enums.AppStatus;
import app.finup.common.exception.ManagerException;

import java.io.UnsupportedEncodingException;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/**
 * 메일 발송 구현체
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class MailProviderImpl implements MailProvider {

    private final JavaMailSender mailSender;

    // 메일 전송 상수
    public static final String FROM_USERNAME = "FinUp";
    public static final String CID_LOGO = "logo";
    public static final ClassPathResource IMAGE_LOGO =
            new ClassPathResource("static/img/png/logo_login.png");

    private static final DateTimeFormatter TIME_FORMATTER =
            DateTimeFormatter.ofPattern("yyyy년 MM월 dd일 HH:mm");

    @Value("${spring.mail.username}")
    private String from;

    @Override
    public void sendVerifyUrl(String to, String verifyUrl, Duration expiration) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(message);

            String title = "[FinUp] 이메일 인증 요청입니다.";
            String content = createVerifyUrlContent(verifyUrl, expiration);
            setHelper(from, to, helper, title, content);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("[MAIL] 인증 URL 이메일 전송 실패! 원인: {}", e.getMessage(), e);
            throw new ManagerException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

    @Override
    public void sendVerifyCode(String to, String verifyCode, Duration expiration) {

        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper = getMimeMessageHelper(message);

            String title = "[FinUp] 인증 코드 요청입니다.";
            String content = createVerifyCodeContent(verifyCode, expiration);
            setHelper(from, to, helper, title, content);

            mailSender.send(message);

        } catch (Exception e) {
            log.error("[MAIL] 인증 코드 이메일 전송 실패! 원인: {}", e.getMessage(), e);
            throw new ManagerException(AppStatus.UTILS_LOGIC_FAILED, e);
        }
    }

    // ---------- 내부 유틸 ----------

    private MimeMessageHelper getMimeMessageHelper(MimeMessage message)
            throws MessagingException {
        return new MimeMessageHelper(message, true, "UTF-8");
    }

    private void setHelper(
            String from,
            String to,
            MimeMessageHelper helper,
            String subject,
            String content
    ) throws MessagingException, UnsupportedEncodingException {

        helper.setFrom(from, FROM_USERNAME); // 발신자
        helper.setTo(to);                    // 수신자
        helper.setSubject(subject);
        helper.setText(content, true);       // HTML 허용

        if (IMAGE_LOGO.exists()) {
            helper.addInline(CID_LOGO, IMAGE_LOGO);
        }
    }

    // URL 인증 메일 내용
    private String createVerifyUrlContent(String verifyUrl, Duration expiration) {
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(expiration.toMinutes());

        return """
                <html>
                  <body>
                    <h2>FinUp 이메일 인증</h2>
                    <p>아래 버튼을 눌러 이메일 인증을 완료해 주세요.</p>
                    <p>
                      <a href="%s" style="display:inline-block;padding:10px 20px;
                         background:#2563eb;color:#ffffff;text-decoration:none;
                         border-radius:4px;">이메일 인증하기</a>
                    </p>
                    <p>유효기간: %s 까지</p>
                    <p>버튼이 동작하지 않으면 아래 링크를 복사해서 브라우저에 붙여넣어 주세요.</p>
                    <p>%s</p>
                  </body>
                </html>
                """.formatted(verifyUrl, expiresAt.format(TIME_FORMATTER), verifyUrl);
    }

    // 코드 인증 메일 내용
    private String createVerifyCodeContent(String verifyCode, Duration expiration) {
        LocalDateTime expiresAt =
                LocalDateTime.now().plusMinutes(expiration.toMinutes());

        return """
                <html>
                  <body>
                    <h2>FinUp 이메일 인증 코드</h2>
                    <p>아래 인증 코드를 화면에 입력해 주세요.</p>
                    <h1 style="letter-spacing:4px;">%s</h1>
                    <p>유효기간: %s 까지</p>
                  </body>
                </html>
                """.formatted(verifyCode, expiresAt.format(TIME_FORMATTER));
    }
}
