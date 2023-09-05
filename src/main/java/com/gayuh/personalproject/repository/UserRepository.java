package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.User;
import com.gayuh.personalproject.query.UserQuery;
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
    @Query(value = """
            select new com.gayuh.personalproject.query.UserQuery(
            u.id,
            u.name,
            u.email,
            u.password,
            u.activated,
            u.suspend,
            u.createdAt,
            u.updatedAt,
            r.id,
            r.name,
            fp.id,
            fp.token,
            fp.expiredAt,
            uv.id,
            uv.token,
            uv.expiredAt
            )
            from User u
            join Role r on u.role.id = r.id
            left join ForgetPassword fp on fp.user.id = u.id
            left join UserVerify uv on uv.user.id = u.id
            where u.email = :email
            """)
    Optional<UserQuery> findUserQueryByEmail(String email);
}
