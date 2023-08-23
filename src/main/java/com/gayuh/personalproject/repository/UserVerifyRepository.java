package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.UserVerify;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserVerifyRepository extends JpaRepository<UserVerify, Long> {
    Optional<UserVerify> findUserVerifiesByToken(String token);
    @Query(value = """
            select uv from UserVerify uv join User u on uv.user.id = u.id where u.email = :email
            """)
    Optional<UserVerify> findUserVerifyByUserEmail(String email);
}
