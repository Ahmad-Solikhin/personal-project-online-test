package com.gayuh.personalproject.service.email;

import jakarta.mail.MessagingException;
import jakarta.mail.internet.MimeMessage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Map;

@Slf4j
@Service
@RequiredArgsConstructor
public class EmailServiceImpl implements EmailService {
    private static final String FROM_EMAIL = "onlinetest@gmail.com";
    private final TemplateEngine templateEngine;
    private final JavaMailSender javaMailSender;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("HH:mm:ss dd-MM-yyyy");
    @Value("${CLIENT_BASE_URL}")
    private String clientBaseUrl;

    @Async
    @Override
    public void sendEmailVerification(String name, String to, String token, LocalDateTime before) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                    "subjectMessage", "Online Test Account Verification",
                    "message", generateMessageEmailVerification(name, before),
                    "url", generateVerifyEmailUrl(token),
                    "button", "Verify",
                    "year", LocalDateTime.now().getYear()
            ));
            getEmailTemplateWithContext(to, context, "New Account Verification");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        log.info("Email verification to {}, successfully send", to);
    }

    @Async
    @Override
    public void sendEmailForgetPassword(String name, String to, String token, LocalDateTime before) {
        try {
            Context context = new Context();
            context.setVariables(Map.of(
                    "subjectMessage", "Online Test Forget Password",
                    "message", generateMessageForgetPassword(name, before),
                    "url", generateForgetPasswordEmailUrl(token),
                    "button", "Forget Password",
                    "year", LocalDateTime.now().getYear()
            ));
            getEmailTemplateWithContext(to, context, "Forget Password");
        } catch (Exception exception) {
            exception.printStackTrace();
        }
        log.info("Forget password to {}, successfully send", to);
    }

    private void getEmailTemplateWithContext(String to, Context context, String subject) throws MessagingException {
        String text = templateEngine.process("emailtemplate", context);
        MimeMessage message = javaMailSender.createMimeMessage();
        MimeMessageHelper helper = new MimeMessageHelper(message, true, "UTF-8");
        helper.setPriority(1);
        helper.setSubject(subject);
        helper.setFrom(FROM_EMAIL);
        helper.setTo(to);
        helper.setText(text, true);
        javaMailSender.send(message);
    }

    private String generateMessageEmailVerification(String name, LocalDateTime before) {
        return "Hello " + name + ", please verify your account by click the button below before " + before.format(formatter);
    }

    private String generateVerifyEmailUrl(String token) {
        return clientBaseUrl + "auth/verify-email?token=" + token;
    }

    private String generateMessageForgetPassword(String name, LocalDateTime before) {
        return "Hello " + name + ", use link below to change your password before " + before.format(formatter);
    }

    private String generateForgetPasswordEmailUrl(String token) {
        return clientBaseUrl + "auth/forgot-password?token=" + token;
    }
}
