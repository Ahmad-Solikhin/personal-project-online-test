package com.gayuh.personalproject.service.email;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
@Slf4j
@Service
public class EmailServiceImpl implements EmailService {
    @Async
    @Override
    public void sendEmailVerification(String name, String to, String token, LocalDateTime before) {

    }

    @Async
    @Override
    public void sendEmailForgetPassword(String name, String to, String token, LocalDateTime before) {

    }
}
