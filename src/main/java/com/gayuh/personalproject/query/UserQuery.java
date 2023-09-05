package com.gayuh.personalproject.query;

import java.time.LocalDateTime;

public record UserQuery(
        String id,
        String name,
        String email,
        String password,
        Boolean activated,
        Boolean suspend,
        LocalDateTime createdAt,
        LocalDateTime updatedAt,
        Long roleId,
        String role,
        Long forgetPasswordId,
        String forgetPasswordToken,
        LocalDateTime forgetPasswordExpiredAt,
        Long userVerifyId,
        String userVerifyToken,
        LocalDateTime userVerifyExpiredAt

) {
}
