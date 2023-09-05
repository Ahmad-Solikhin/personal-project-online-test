package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.dto.TestHistoryResponse;
import com.gayuh.personalproject.dto.UserTestHistoryResponse;
import com.gayuh.personalproject.entity.TestHistory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
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
}
