package com.gayuh.personalproject.controller;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.gayuh.personalproject.dto.*;
import com.gayuh.personalproject.entity.ForgetPassword;
import com.gayuh.personalproject.entity.User;
import com.gayuh.personalproject.entity.UserVerify;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.repository.ForgetPasswordRepository;
import com.gayuh.personalproject.repository.RoleRepository;
import com.gayuh.personalproject.repository.UserRepository;
import com.gayuh.personalproject.repository.UserVerifyRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.web.servlet.MockMvc;

import java.time.LocalDateTime;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

@SpringBootTest
@TestPropertySource("classpath:application-test.yml")
@ActiveProfiles("test")
@AutoConfigureMockMvc
class AuthControllerTest {

    @Autowired
    private MockMvc mockMvc;
    @Autowired
    private ForgetPasswordRepository forgetPasswordRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private RoleRepository roleRepository;
    @Autowired
    private UserVerifyRepository userVerifyRepository;
    @Autowired
    private ObjectMapper objectMapper;
    @Autowired
    private PasswordEncoder passwordEncoder;

    private User staticUser;

    @BeforeEach
    void setUp() {
        forgetPasswordRepository.deleteAll();
        userVerifyRepository.deleteAll();
        userRepository.deleteAll();

        staticUser = User.builder()
                .email("test@gmail.com")
                .name("Gayuh")
                .role(roleRepository.findById(2L).orElseThrow())
                .activated(true)
                .createdAt(LocalDateTime.now())
                .updatedAt(LocalDateTime.now())
                .suspend(false)
                .password(passwordEncoder.encode("rahasia"))
                .build();
        userRepository.save(staticUser);
    }

    @Test
    void loginSuccess() throws Exception {
        LoginRequest request = new LoginRequest("test@gmail.com", "rahasia");
        String stringRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/auth/login")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(ResponseMessage.LOGIN_SUCCESS.value(), response.getMessage());
        });
    }

    @Test
    void registerSuccess() throws Exception {

        RegisterRequest request = new RegisterRequest("Ahmad", "ahmad@gmail.com", "Rahasia@123", "Rahasia@123");
        String stringRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isCreated()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(ResponseMessage.REGISTER_SUCCESS.value(), response.getMessage());
        });
    }

    @Test
    void registerBadRequestEmailAlreadyExist() throws Exception {

        RegisterRequest request = new RegisterRequest("Ahmad", "test@gmail.com", "Rahasia@123", "Rahasia@123");
        String stringRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(ResponseMessage.ACCOUNT_ALREADY_EXIST.value(), response.getMessage());
        });
    }

    @Test
    void registerBadRequestPasswordNotMatch() throws Exception {

        RegisterRequest request = new RegisterRequest("Ahmad", "huwalahumba@gmail.com", "Rahasia@123", "Rahasia@321");
        String stringRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertEquals(ResponseMessage.PASSWORD_NOT_SAME.value(), response.getMessage());
        });
    }

    @Test
    void registerBadRequestWrongPasswordFormat() throws Exception {

        RegisterRequest request = new RegisterRequest("Ahmad", "random@gmail.com", "Rahasia1123", "Rahasia1123");
        String stringRequest = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                post("/api/v1/auth/register")
                        .accept(MediaType.APPLICATION_JSON)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(stringRequest)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, RegisterRequest> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNull(response.getData());
            assertNotNull(response.getMessage());
            assertEquals(response.getMessage().password(), ResponseMessage.PASSWORD_REGEX_NOT_MATCH.value());
        });
    }

    @Test
    void verifyAccountSuccess() throws Exception {
        registerSuccess();

        User user = userRepository.getUserByEmail("ahmad@gmail.com").orElseThrow();
        UserVerify userVerify = user.getUserVerify();

        mockMvc.perform(
                put("/api/v1/auth/verify-email")
                        .param("token", userVerify.getToken())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.ACTIVATE_ACCOUNT_SUCCESS.value(), response.getMessage());
        });
    }

    @Test
    void verifyAccountNotFound() throws Exception {
        mockMvc.perform(
                put("/api/v1/auth/verify-email")
                        .param("token", UUID.randomUUID().toString())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
        });
    }

    @Test
    void verifyAccountAlreadyActivated() throws Exception {
        UserVerify userVerify = userVerifyRepository.save(UserVerify.builder()
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().plusHours(5))
                .user(staticUser)
                .build());

        mockMvc.perform(
                put("/api/v1/auth/verify-email")
                        .param("token", userVerify.getToken())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.ACCOUNT_ALREADY_ACTIVATED.value(), response.getMessage());
        });
    }

    @Test
    void verifyAccountAlreadyExpired() throws Exception {
        staticUser.setActivated(false);
        userRepository.save(staticUser);

        UserVerify userVerify = userVerifyRepository.save(UserVerify.builder()
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().minusDays(1L))
                .user(staticUser)
                .build());

        System.out.println(userVerify.getExpiredAt().isBefore(LocalDateTime.now()));

        mockMvc.perform(
                put("/api/v1/auth/verify-email")
                        .param("token", userVerify.getToken())
                        .accept(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });
            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.TOKEN_EXPIRED.value(), response.getMessage());
        });
    }

    @Test
    void resendEmailVerificationSuccess() throws Exception {
        staticUser.setActivated(false);
        userRepository.save(staticUser);

        UserVerify userVerify = userVerifyRepository.save(UserVerify.builder()
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().minusDays(1L))
                .user(staticUser)
                .build());

        userVerifyRepository.save(userVerify);

        ResendEmailRequest request = new ResendEmailRequest(staticUser.getEmail());
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/auth/resend-verify-email")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.SUCCESS_RESEND_EMAIL.value(), response.getMessage());
        });
    }

    @Test
    void resendEmailVerificationAlreadyActivated() throws Exception {
        staticUser.setActivated(true);
        userRepository.save(staticUser);

        UserVerify userVerify = userVerifyRepository.save(UserVerify.builder()
                .token(UUID.randomUUID().toString())
                .expiredAt(LocalDateTime.now().minusDays(1L))
                .user(staticUser)
                .build());

        userVerifyRepository.save(userVerify);

        ResendEmailRequest request = new ResendEmailRequest(staticUser.getEmail());
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/auth/resend-verify-email")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.ACCOUNT_ALREADY_ACTIVATED.value(), response.getMessage());
        });
    }

    @Test
    void resendEmailVerificationNotFound() throws Exception {

        ResendEmailRequest request = new ResendEmailRequest("undefined@gmail.com");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/auth/resend-verify-email")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
        });
    }

    @Test
    void forgotPasswordSuccess() throws Exception {
        ResendEmailRequest request = new ResendEmailRequest(staticUser.getEmail());
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/auth/forgot-password")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.SUCCESS_RESEND_EMAIL.value(), response.getMessage());
        });
    }

    @Test
    void forgotPasswordNotFound() throws Exception {
        ResendEmailRequest request = new ResendEmailRequest("undefined@gmail.com");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/auth/forgot-password")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
        });
    }

    @Test
    void forgotPasswordAccountSuspend() throws Exception {
        staticUser.setSuspend(true);
        userRepository.save(staticUser);

        ResendEmailRequest request = new ResendEmailRequest(staticUser.getEmail());
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                put("/api/v1/auth/forgot-password")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isLocked()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.ACCOUNT_SUSPEND.value(), response.getMessage());
        });
    }

    @Test
    void changePasswordSuccess() throws Exception {
        forgotPasswordSuccess();
        ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByUserEmail(staticUser.getEmail())
                .orElseThrow();

        ForgetPasswordRequest request = new ForgetPasswordRequest("Rahasia@123", "Rahasia@123");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/v1/auth/forgot-password")
                        .param("token", forgetPassword.getToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isOk()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.SUCCESS_CHANGE_PASSWORD.value(), response.getMessage());
        });
    }

    @Test
    void changePasswordSameWithOldPassword() throws Exception {
        forgotPasswordSuccess();
        ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByUserEmail(staticUser.getEmail())
                .orElseThrow();

        staticUser.setPassword(passwordEncoder.encode("Rahasia@123"));
        userRepository.save(staticUser);

        ForgetPasswordRequest request = new ForgetPasswordRequest("Rahasia@123", "Rahasia@123");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/v1/auth/forgot-password")
                        .param("token", forgetPassword.getToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.PASSWORD_SAME_OLD_PASSWORD.value(), response.getMessage());
        });
    }

    @Test
    void changePasswordPasswordNotSame() throws Exception {
        forgotPasswordSuccess();
        ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByUserEmail(staticUser.getEmail())
                .orElseThrow();

        ForgetPasswordRequest request = new ForgetPasswordRequest("Rahasia@123", "Rahasia@321");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/v1/auth/forgot-password")
                        .param("token", forgetPassword.getToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.PASSWORD_NOT_SAME.value(), response.getMessage());
        });
    }

    @Test
    void changePasswordWrongFormat() throws Exception {
        forgotPasswordSuccess();
        ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByUserEmail(staticUser.getEmail())
                .orElseThrow();

        ForgetPasswordRequest request = new ForgetPasswordRequest("Rahasia", "Rahasia");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/v1/auth/forgot-password")
                        .param("token", forgetPassword.getToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, ForgetPasswordRequest> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(response.getMessage().password(), ResponseMessage.PASSWORD_REGEX_NOT_MATCH.value());
        });
    }

    @Test
    void changePasswordExpired() throws Exception {
        forgotPasswordSuccess();
        ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByUserEmail(staticUser.getEmail())
                .orElseThrow();

        forgetPassword.setExpiredAt(LocalDateTime.now().minusDays(1L));
        forgetPasswordRepository.save(forgetPassword);

        ForgetPasswordRequest request = new ForgetPasswordRequest("Rahasia@123", "Rahasia@123");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/v1/auth/forgot-password")
                        .param("token", forgetPassword.getToken())
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isBadRequest()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.TOKEN_EXPIRED.value(), response.getMessage());
        });
    }

    @Test
    void changePasswordNotFount() throws Exception {
        ForgetPasswordRequest request = new ForgetPasswordRequest("Rahasia@123", "Rahasia@123");
        String requestString = objectMapper.writeValueAsString(request);

        mockMvc.perform(
                patch("/api/v1/auth/forgot-password")
                        .param("token", "undefined")
                        .accept(MediaType.APPLICATION_JSON)
                        .content(requestString)
                        .contentType(MediaType.APPLICATION_JSON)
        ).andExpectAll(
                status().isNotFound()
        ).andDo(result -> {
            WebResponse<String, String> response = objectMapper.readValue(result.getResponse().getContentAsString(), new TypeReference<>() {
            });

            assertNotNull(response.getMessage());
            assertEquals(ResponseMessage.DATA_NOT_FOUND.value(), response.getMessage());
        });
    }
}
