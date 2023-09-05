package com.gayuh.personalproject.service.auth;

import com.gayuh.personalproject.dto.ForgetPasswordRequest;
import com.gayuh.personalproject.dto.LoginRequest;
import com.gayuh.personalproject.dto.RegisterRequest;
import com.gayuh.personalproject.dto.ResendEmailRequest;
import com.gayuh.personalproject.entity.ForgetPassword;
import com.gayuh.personalproject.entity.Role;
import com.gayuh.personalproject.entity.User;
import com.gayuh.personalproject.entity.UserVerify;
import com.gayuh.personalproject.enumerated.ResponseMessage;
import com.gayuh.personalproject.query.UserQuery;
import com.gayuh.personalproject.repository.ForgetPasswordRepository;
import com.gayuh.personalproject.repository.RoleRepository;
import com.gayuh.personalproject.repository.UserRepository;
import com.gayuh.personalproject.repository.UserVerifyRepository;
import com.gayuh.personalproject.service.JwtService;
import com.gayuh.personalproject.service.ParentService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class AuthServiceImpl extends ParentService implements AuthService {

    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final RoleRepository roleRepository;
    private final UserVerifyRepository userVerifyRepository;
    private final ForgetPasswordRepository forgetPasswordRepository;

    @Override
    public String login(LoginRequest request) {
        UserQuery query = userRepository.findUserQueryByEmail(request.email()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.LOGIN_FAILED.value())
        );

        if (!passwordEncoder.matches(request.password(), query.password())) {
            throw new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.LOGIN_FAILED.value());
        }

        if (Boolean.TRUE.equals(query.suspend())) {
            throw new ResponseStatusException(HttpStatus.LOCKED, ResponseMessage.ACCOUNT_SUSPEND.value());
        }

        if (Boolean.FALSE.equals(query.activated())) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED, ResponseMessage.ACCOUNT_INACTIVE.value());
        }

        return jwtService.generateToken(query);
    }

    @Override
    public void register(RegisterRequest request) {
        validationService.validate(request);

        userRepository.getUserByEmail(request.email()).ifPresent(user -> {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.ACCOUNT_ALREADY_EXIST.value());
        });

        if (!request.password().equals(request.retypedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.PASSWORD_NOT_SAME.value());
        }

        Role role = roleRepository.findById(2L).orElseThrow();

        LocalDateTime currentTime = LocalDateTime.now();

        User user = User.builder()
                .role(role)
                .name(request.name())
                .suspend(false)
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .activated(false)
                .updatedAt(currentTime)
                .createdAt(currentTime)
                .build();
        userRepository.save(user);

        UserVerify userVerify = UserVerify.builder()
                .user(user)
                .expiredAt(LocalDateTime.now().plusHours(3L))
                .token(UUID.randomUUID().toString())
                .build();
        userVerifyRepository.save(userVerify);

        //Todo : Send email for account verification
    }

    @Override
    public void verifyEmail(String token) {
        UserVerify userVerify = userVerifyRepository.findUserVerifiesByToken(token).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );

        User user = userVerify.getUser();

        checkActivatedUser(user);

        if (userVerify.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.TOKEN_EXPIRED.value());
        }

        user.setActivated(true);
        userRepository.save(user);
    }

    @Override
    public void resendVerifyEmail(ResendEmailRequest request) {
        validationService.validate(request);

        UserVerify userVerify = userVerifyRepository.findUserVerifyByUserEmail(request.email()).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );

        if (Boolean.TRUE.equals(userVerify.getUser().getSuspend())) {
            throw new ResponseStatusException(HttpStatus.LOCKED, ResponseMessage.ACCOUNT_SUSPEND.value());
        }

        checkActivatedUser(userVerify.getUser());

        userVerify.setToken(UUID.randomUUID().toString());
        userVerify.setExpiredAt(LocalDateTime.now().plusHours(3L));
        userVerifyRepository.save(userVerify);

        //Todo : Send email for new token

    }

    @Override
    public void forgotPassword(ResendEmailRequest request) {
        validationService.validate(request);

        forgetPasswordRepository.findForgetPasswordByUserEmail(request.email()).ifPresentOrElse(forgetPassword -> {
            forgetPassword.setToken(UUID.randomUUID().toString());
            forgetPassword.setExpiredAt(LocalDateTime.now().plusHours(3L));
            forgetPasswordRepository.save(forgetPassword);
            //Todo : Send email for change password
        }, () -> {
            User user = userRepository.getUserByEmail(request.email()).orElseThrow(
                    () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
            );

            if (Boolean.TRUE.equals(user.getSuspend())) {
                throw new ResponseStatusException(HttpStatus.LOCKED, ResponseMessage.ACCOUNT_SUSPEND.value());
            }

            forgetPasswordRepository.save(ForgetPassword.builder()
                    .expiredAt(LocalDateTime.now().plusHours(3L))
                    .token(UUID.randomUUID().toString())
                    .user(user)
                    .build());
            //Todo : Send email for change password
        });

    }

    @Override
    public void changePasswordFromToken(String token, ForgetPasswordRequest request) {
        ForgetPassword forgetPassword = forgetPasswordRepository.findForgetPasswordByToken(token).orElseThrow(
                () -> new ResponseStatusException(HttpStatus.NOT_FOUND, ResponseMessage.DATA_NOT_FOUND.value())
        );

        if (forgetPassword.getExpiredAt().isBefore(LocalDateTime.now())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.TOKEN_EXPIRED.value());
        }

        validationService.validate(request);

        if (!request.password().equals(request.retypedPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.PASSWORD_NOT_SAME.value());
        }

        forgetPassword.setExpiredAt(LocalDateTime.now());
        forgetPasswordRepository.save(forgetPassword);

        User user = forgetPassword.getUser();

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.PASSWORD_SAME_OLD_PASSWORD.value());
        }

        user.setPassword(passwordEncoder.encode(request.password()));
        user.setUpdatedAt(LocalDateTime.now());
        userRepository.save(user);
    }

    private void checkActivatedUser(User user) {
        if (Boolean.TRUE.equals(user.getActivated())) {
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, ResponseMessage.ACCOUNT_ALREADY_ACTIVATED.value());
        }
    }
}
