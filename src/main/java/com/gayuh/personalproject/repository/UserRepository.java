package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, String> {
}
