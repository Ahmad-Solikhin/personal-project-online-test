package com.gayuh.personalproject.configuration;

import com.gayuh.personalproject.resolver.AdminArgumentResolver;
import com.gayuh.personalproject.resolver.UserArgumentResolver;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.method.support.HandlerMethodArgumentResolver;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.List;

@Configuration
@RequiredArgsConstructor
public class ApplicationConfiguration implements WebMvcConfigurer {

    private final AdminArgumentResolver adminArgumentResolver;
    private final UserArgumentResolver userArgumentResolver;

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Override
    public void addArgumentResolvers(List<HandlerMethodArgumentResolver> resolvers) {
        WebMvcConfigurer.super.addArgumentResolvers(resolvers);
        resolvers.addAll(List.of(adminArgumentResolver, userArgumentResolver));
    }
}
