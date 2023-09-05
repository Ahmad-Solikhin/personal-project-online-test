package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.QuestionTitle;
import com.gayuh.personalproject.query.QuestionTitleQuery;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface QuestionTitleRepository extends JpaRepository<QuestionTitle, String> {
    @Query(value = """
            select
            new com.gayuh.personalproject.query.QuestionTitleQuery(
            vw.id,
            vw.title,
            vw.token,
            vw.started,
            vw.createdAt as recent,
            vw.updatedAt,
            vw.userId,
            vw.userName,
            vw.topicId,
            vw.topicName,
            vw.difficultyId,
            vw.difficultyName,
            vw.accessId,
            vw.accessName,
            vw.tested
            )
            from QuestionTitleView vw
            where vw.id = :id
            """)
    Optional<QuestionTitleQuery> findQuestionTitleQuery(String id);

    @Modifying
    @Query(value = """
            delete from QuestionTitle qt where qt.id = :questionTitleId
            """)
    void deleteQuestionTitleById(String questionTitleId);

    @Query(value = """
            select
            new com.gayuh.personalproject.query.QuestionTitleQuery(
            vw.id,
            vw.title,
            vw.token,
            vw.started,
            vw.createdAt as recent,
            vw.updatedAt,
            vw.userId,
            vw.userName,
            vw.topicId,
            vw.topicName,
            vw.difficultyId,
            vw.difficultyName,
            vw.accessId,
            vw.accessName,
            vw.tested
            )
            from QuestionTitleView vw
            where vw.accessId = 1
            and lower(vw.title) like lower(:search)
            and case when (:topicId != 0) then (vw.topicId = :topicId) else (vw.topicId != 0) end
            and case when (:difficultyId != 0) then (vw.difficultyId = :difficultyId) else (vw.difficultyId != 0) end
            """)
    Page<QuestionTitleQuery> findAllPublicQuestionTitleWithPageResult(String search, Long topicId, Long difficultyId, PageRequest pageRequest);

    @Query(value = """
            select
            new com.gayuh.personalproject.query.QuestionTitleQuery(
            vw.id,
            vw.title,
            vw.token,
            vw.started,
            vw.createdAt as recent,
            vw.updatedAt,
            vw.userId,
            vw.userName,
            vw.topicId,
            vw.topicName,
            vw.difficultyId,
            vw.difficultyName,
            vw.accessId,
            vw.accessName,
            vw.tested
            )
            from QuestionTitleView vw
            where vw.userId = :userId
            and lower(vw.title) like lower(:search)
            and case when (:topicId != 0) then (vw.topicId = :topicId) else (vw.topicId != 0) end
            and case when (:difficultyId != 0) then (vw.difficultyId = :difficultyId) else (vw.difficultyId != 0) end
            and case when (:accessId != 0) then (vw.accessId = :accessId) else (vw.accessId != 0) end
            """)
    Page<QuestionTitleQuery> findAllQuestionTitleCreatedByUserWithPageResult(String userId, String search, Long topicId, Long difficultyId, Long accessId, PageRequest pageRequest);
    @Query(value = """
            select
            new com.gayuh.personalproject.query.QuestionTitleQuery(
            vw.id,
            vw.title,
            vw.token,
            vw.started,
            vw.createdAt as recent,
            vw.updatedAt,
            vw.userId,
            vw.userName,
            vw.topicId,
            vw.topicName,
            vw.difficultyId,
            vw.difficultyName,
            vw.accessId,
            vw.accessName,
            vw.tested
            )
            from QuestionTitleView vw
            where vw.token = :token
            """)
    Optional<QuestionTitleQuery> findQuestionTitleQueryByToken(String token);
}
