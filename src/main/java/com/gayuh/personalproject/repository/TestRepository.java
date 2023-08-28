package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Test;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface TestRepository extends JpaRepository<Test, String> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Test t where t.question.id in :idQuestions
            """)
    void deleteAllTestByQuestionId(List<Long> idQuestions);
}
