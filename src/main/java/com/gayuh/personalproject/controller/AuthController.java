package com.gayuh.personalproject.controller;

import com.gayuh.personalproject.dto.ForgetPasswordRequest;
import com.gayuh.personalproject.dto.LoginRequest;
import com.gayuh.personalproject.dto.RegisterRequest;
import com.gayuh.personalproject.dto.ResendEmailRequest;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.service.auth.AuthService;
import com.gayuh.personalproject.util.CustomResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("api/v1/auth")
@RequiredArgsConstructor
public class AuthController {
    private final AuthService authService;

    @PostMapping(value = "login")
    public ResponseEntity<Object> login(
            @RequestBody LoginRequest request
    ) {
        String response = authService.login(request);

        return CustomResponse.generateResponse(
                ResponseMessage.LOGIN_SUCCESS.value(),
                HttpStatus.OK,
                response
        );
    }

    @PostMapping(value = "register")
    public ResponseEntity<Object> register(
            @RequestBody RegisterRequest request
    ) {
        authService.register(request);

        return CustomResponse.generateResponse(
                ResponseMessage.REGISTER_SUCCESS.value(),
                HttpStatus.CREATED
        );
    }

    @PutMapping(value = "verify-email")
    public ResponseEntity<Object> verifyEmail(
            @RequestParam(name = "token") String token
    ) {
        authService.verifyEmail(token);

        return CustomResponse.generateResponse(
                ResponseMessage.ACTIVATE_ACCOUNT_SUCCESS.value(),
                HttpStatus.OK
        );
    }

    @PutMapping(value = "resend-verify-email")
    public ResponseEntity<Object> resendVerifyEmail(
            @RequestBody ResendEmailRequest request
    ) {
        authService.resendVerifyEmail(request);

        return CustomResponse.generateResponse(
                ResponseMessage.SUCCESS_RESEND_EMAIL.value(),
                HttpStatus.OK
        );
    }

    @PutMapping(value = "forgot-password")
    public ResponseEntity<Object> forgotPassword(
            @RequestBody ResendEmailRequest request
    ){
        authService.forgotPassword(request);

        return CustomResponse.generateResponse(
                ResponseMessage.SUCCESS_RESEND_EMAIL.value(),
                HttpStatus.OK
        );
    }

    @PatchMapping(value = "forgot-password")
    public ResponseEntity<Object> changePassword(
            @RequestParam(name = "token") String token,
            @RequestBody ForgetPasswordRequest request
            ){
        authService.changePasswordFromToken(token, request);

        return CustomResponse.generateResponse(
                ResponseMessage.SUCCESS_CHANGE_PASSWORD.value(),
                HttpStatus.OK
        );
    }

}
