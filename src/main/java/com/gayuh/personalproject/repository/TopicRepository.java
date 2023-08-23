package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Topic;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TopicRepository extends JpaRepository<Topic, Long> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Topic t where t.id > 3
            """)
    void deleteAllExceptSeeders();
}
