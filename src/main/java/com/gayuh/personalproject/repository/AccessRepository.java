package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Access;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface AccessRepository extends JpaRepository<Access, Long> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Access a where a.id > 3
            """)
    void deleteAllExceptSeeders();
}
