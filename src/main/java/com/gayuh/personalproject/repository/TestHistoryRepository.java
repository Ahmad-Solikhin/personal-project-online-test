package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.TestHistory;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

public interface TestHistoryRepository extends JpaRepository<TestHistory, String> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from TestHistory th where th.questionTitle.id = :questionTitleId
            """)
    void deleteAllTestHistoryByQuestionTitleId(String questionTitleId);
}
