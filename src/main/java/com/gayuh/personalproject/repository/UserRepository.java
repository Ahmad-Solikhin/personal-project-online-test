package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {
    @Query(value = """
            select u from User u where u.email = :email
            """)
    Optional<User> getUserByEmail(String email);

    @Query(value = """
            select u from User u where u.id = :userId
            """)
    Optional<User> getUserById(String userId);

    @Modifying
    @Transactional
    @Query(value = """
            delete from User u where u.email <> 'admin@gmail.com'
            """)
    void deleteAllExceptSeeder();
}
