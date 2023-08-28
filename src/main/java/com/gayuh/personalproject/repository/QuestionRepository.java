package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Question;
import com.gayuh.personalproject.query.QuestionQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

public interface QuestionRepository extends JpaRepository<Question, Long> {
    @Query(value = """
            select q.id from Question q where q.questionTitle.id = :questionTitleId
            """)
    List<Long> findAllQuestionIdByQuestionTitleId(String questionTitleId);

    @Transactional
    @Modifying
    @Query(value = """
            delete from Question q where q.questionTitle.id = :questionTitleId
            """)
    void deleteAllQuestionByQuestionTitleId(String questionTitleId);

    @Query(value = """
            select new com.gayuh.personalproject.query.QuestionQuery(
            vw.id,
            vw.questionTitleId,
            vw.time,
            vw.score,
            vw.createdAt,
            vw.updatedAt,
            vw.mediaId
            )
            from QuestionView vw
            where vw.questionTitleId = :questionTitleId
            """)
    List<QuestionQuery> getAllQuestionByQuestionTitleId(String questionTitleId);
}
