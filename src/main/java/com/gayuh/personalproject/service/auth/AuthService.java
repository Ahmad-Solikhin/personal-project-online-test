package com.gayuh.personalproject.service.auth;

import com.gayuh.personalproject.dto.ForgetPasswordRequest;
import com.gayuh.personalproject.dto.LoginRequest;
import com.gayuh.personalproject.dto.RegisterRequest;
import com.gayuh.personalproject.dto.ResendEmailRequest;
import org.springframework.transaction.annotation.Transactional;

public interface AuthService {

    String login(LoginRequest request);

    @Transactional
    void register(RegisterRequest request);

    @Transactional
    void verifyEmail(String token);

    @Transactional
    void resendVerifyEmail(ResendEmailRequest request);

    @Transactional
    void forgotPassword(ResendEmailRequest request);

    @Transactional
    void changePasswordFromToken(String token, ForgetPasswordRequest request);
}
