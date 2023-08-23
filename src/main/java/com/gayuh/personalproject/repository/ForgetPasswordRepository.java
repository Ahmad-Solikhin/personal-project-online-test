package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.ForgetPassword;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface ForgetPasswordRepository extends JpaRepository<ForgetPassword, Long> {
    @Query(value = """
            select fp from ForgetPassword fp where fp.user.email = :email
            """)
    Optional<ForgetPassword> findForgetPasswordByUserEmail(String email);

    Optional<ForgetPassword> findForgetPasswordByToken(String token);
}
