package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Test;
import com.gayuh.personalproject.query.TestQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface TestRepository extends JpaRepository<Test, String> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Test t where t.question.id in :idQuestions
            """)
    void deleteAllTestByQuestionId(List<Long> idQuestions);

    @Modifying
    @Query(value = """
            delete from Test t where t.question.id = :questionId
            """)
    void deleteTestByQuestionId(Long questionId);

    @Query(value = """
            select new com.gayuh.personalproject.query.TestQuery(
            id,
            choice.id,
            question.id,
            testHistory.id
            )
            from Test
            where id = :testId
            """)
    Optional<TestQuery> findTestQueryById(String testId);

    @Query(value = """
            select cast(
                coalesce(
                    sum(case when coalesce(c.correct, false) then q.score else 0 end), 0
                    )
                as float
            ) / cast(sum(q.score) as float) * 100.0
            from Test t
            join TestHistory th on t.testHistory.id = th.id
            join Question q on q.id = t.question.id
            left join Choice c on t.choice.id = c.id
            where th.id = :testHistoryId
            group by th.id
            """)
    Double findFinalScoreByTestHistoryId(String testHistoryId);

    @Query(value = """
                select t.test_id from tests t where t.test_history_id = :testHistoryId limit 1
            """, nativeQuery = true)
    String findFirstIdByTestHistoryId(String testHistoryId);
}
