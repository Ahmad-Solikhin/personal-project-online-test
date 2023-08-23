package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface RoleRepository extends JpaRepository<Role, Long> {
    @Modifying
    @Transactional
    @Query(value = """
            delete from Role r where r.id > 3
            """)
    void deleteAllExceptSeeders();
}
