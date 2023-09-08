package com.gayuh.personalproject.configuration;

import com.gayuh.personalproject.resolver.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;
import java.util.Properties;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration implements WebMvcConfigurer {
    private final UserArgumentResolver userArgumentResolver;
    private static final String TIMEOUT_THREAD = "60000";
    @Value("${EMAIL_USERNAME}")
    private String emailUsername;
    @Value("${EMAIL_PASSWORD}")
    private String emailPassword;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public JavaMailSender javaMailSender() {
        JavaMailSenderImpl mailSender = new JavaMailSenderImpl();
        mailSender.setHost("smtp.gmail.com");
        mailSender.setPort(587);

        mailSender.setUsername(emailUsername);
        mailSender.setPassword(emailPassword);

        mailSender.setDefaultEncoding("utf-8");

        Properties props = mailSender.getJavaMailProperties();
        props.put("mail.mime.charset", "UTF");
        props.put("mail.smtp.writetimeout", TIMEOUT_THREAD);
        props.put("mail.smtp.connectiontimeout", TIMEOUT_THREAD);
        props.put("mail.smtp.timeout", TIMEOUT_THREAD);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.required", "true");
        props.put("mail.smtp.starttls.enable", "true");

        return mailSender;
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.add(userArgumentResolver);
    }
}
