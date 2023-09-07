package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.dto.TestHistoryQuestionResponse;
import com.gayuh.personalproject.dto.TestHistoryResponse;
import com.gayuh.personalproject.dto.UserTestHistoryResponse;
import com.gayuh.personalproject.entity.TestHistory;
import com.gayuh.personalproject.query.TestHistoryQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface TestHistoryRepository extends JpaRepository<TestHistory, String> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from TestHistory th where th.questionTitle.id = :questionTitleId
            """)
    void deleteAllTestHistoryByQuestionTitleId(String questionTitleId);

    @Query(value = """
            select new com.gayuh.personalproject.dto.TestHistoryResponse(
            th.id, th.startedAt, th.finishedAt, th.score, qt.id, qt.title, t.name, d.name
            )
            from TestHistory th
            join QuestionTitle qt on th.questionTitle.id = qt.id
            join User u on th.user.id = u.id
            join Topic t on qt.topic.id = t.id
            join Difficulty d on qt.difficulty.id = d.id
            where u.id = :userId
            and lower(qt.title) like lower(:search)
            and t.id = COALESCE(:topicId, t.id)
            and d.id = COALESCE(:difficultyId, d.id)
            """)
    Page<TestHistoryResponse> findAllTestHistoryByUserIdWithPage(String search, Long topicId, Long difficultyId, String userId, PageRequest pageRequest);

    @Query(value = """
            select new com.gayuh.personalproject.dto.UserTestHistoryResponse(
            vw.id,
            vw.score,
            vw.startedAt,
            vw.finishedAt,
            vw.time,
            vw.name,
            vw.email
            )
            from TestHistoryView vw
            where vw.questionTitleId = :questionTitleId
            and lower(vw.title) like lower(:search)
            """)
    Page<UserTestHistoryResponse> findAllTestHistoryByQuestionTitleId(String questionTitleId, String search, PageRequest pageRequest);

    @Query(value = """
            select new com.gayuh.personalproject.query.TestHistoryQuery(
            th.id,
            th.startedAt,
            th.finishedAt,
            th.score,
            u.email,
            th.questionTitle.id
            )
            from TestHistory th
            join User u on th.user.id = u.id
            where th.id = :testHistoryId
            """)
    Optional<TestHistoryQuery> findTestHistoryQueryById(String testHistoryId);

    @Modifying
    @Query(value = """
            update TestHistory set score = :score, finishedAt = :finish
            where id = :testHistoryId
            """)
    void updateTestHistoryScoreById(Double score, String testHistoryId, LocalDateTime finish);

    @Query(value = """
            select new com.gayuh.personalproject.dto.TestHistoryQuestionResponse(
            t.question.id,
            t.choice.id
            )
            from TestHistory th
            join Test t on t.testHistory.id = th.id
            where th.id = :testHistoryId
            """)
    List<TestHistoryQuestionResponse> findAllHistoryAnswerById(String testHistoryId);
    @Query(value = """
            select th.test_history_id 
            from test_histories th
            join tests t on t.test_history_id = th.test_history_id
            join questions q on q.question_id = t.question_id
            where th.finished_at is null
            group by th.test_history_id
            having th.started_at + make_interval(secs => sum(q.time)) < :now
            """, nativeQuery = true)
    List<String> findUnfinishedAndTimeIsExpired(LocalDateTime now);
}
