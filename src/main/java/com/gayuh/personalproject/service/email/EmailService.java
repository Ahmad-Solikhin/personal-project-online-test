package com.gayuh.personalproject.service.email;

import java.time.LocalDateTime;

public interface EmailService {

    void sendEmailVerification(String name, String to, String token, LocalDateTime before);
    void sendEmailForgetPassword(String name, String to, String token, LocalDateTime before);
}
