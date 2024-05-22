package com.gayuh.personalproject.configuration;

import com.gayuh.personalproject.resolver.UserArgumentResolver;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.event.ContextRefreshedEvent;
import org.springframework.context.event.EventListener;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.JavaMailSenderImpl;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;
import org.springframework.web.servlet.mvc.method.annotation.RequestMappingHandlerMapping;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Properties;
import java.util.TimeZone;

@Configuration
@RequiredArgsConstructor
@Slf4j
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

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("Asia/Jakarta"));

        log.info("Current time : {}", LocalDateTime.now());
    }

    //Use for listing all endpoint use in this service
    @EventListener
    public void handleContextRefresh(ContextRefreshedEvent event) {
        ApplicationContext applicationContext = event.getApplicationContext();
        applicationContext.getBean(RequestMappingHandlerMapping.class)
                .getHandlerMethods().forEach((key, value) -> log.info("Method {} => {} => {}", key.getMethodsCondition().getMethods(), key, value.getMethod().getParameters()));
    }

    //Print out message when the service is shut down
    @PreDestroy
    public void beforeDestroy() {
        log.info("Online Test Service is Shutdown");
    }

}
