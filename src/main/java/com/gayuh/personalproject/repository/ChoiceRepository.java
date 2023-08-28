package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Choice;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Choice c where c.question.id in :idQuestions
            """)
    void deleteAllChoiceByQuestionId(List<Long> idQuestions);
}
