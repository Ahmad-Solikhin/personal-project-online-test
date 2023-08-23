package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Difficulty;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface DifficultyRepository extends JpaRepository<Difficulty, Long> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Difficulty d where d.id > 3
            """)
    void deleteAllExceptSeeders();
}
