package app.finup.config;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;

import java.util.Properties;

@Slf4j
@Configuration
public class MailConfig {

    @Value("${spring.mail.host}")
    private String host;

    @Value("${spring.mail.port}")
    private int port;

    @Value("${spring.mail.username}")
    private String username;

    @Value("${spring.mail.password}")
    private String password;

    @Value("${spring.mail.properties.mail.debug}")
    private String debug;

    @Value("${spring.mail.properties.mail.transport.protocol}")
    private String transportProtocol;

    @Value("${spring.mail.properties.mail.smtp.auth}")
    private String smtpAuth;

    @Value("${spring.mail.properties.mail.smtp.starttls.enable}")
    private String startTlsEnable;

    @Value("${spring.mail.properties.mail.smtp.ssl.trust}")
    private String sslTrust;

    @Value("${spring.mail.properties.mail.smtp.ssl.enable}")
    private String sslEnable;

    @Bean // JavaMailSender 빈 등록
    public JavaMailSender javaMailService() {
        JavaMailSenderImpl javaMailSender = new JavaMailSenderImpl();
        javaMailSender.setHost(host);
        javaMailSender.setPort(port);
        javaMailSender.setUsername(username);
        javaMailSender.setPassword(password);
        javaMailSender.setJavaMailProperties(setMailProperties());
        return javaMailSender;
    }

    private Properties setMailProperties() {
        Properties properties = new Properties();
        properties.setProperty("mail.transport.protocol", transportProtocol);
        properties.setProperty("mail.debug", debug);
        properties.setProperty("mail.smtp.auth", smtpAuth);
        properties.setProperty("mail.smtp.starttls.enable", startTlsEnable);
        properties.setProperty("mail.smtp.ssl.trust", sslTrust);
        properties.setProperty("mail.smtp.ssl.enable", sslEnable);
        return properties;
    }
}