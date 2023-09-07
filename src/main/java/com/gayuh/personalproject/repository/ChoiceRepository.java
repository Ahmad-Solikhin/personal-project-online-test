package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.dto.ChoiceResponse;
import com.gayuh.personalproject.entity.Choice;
import com.gayuh.personalproject.query.ChoiceQuery;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

public interface ChoiceRepository extends JpaRepository<Choice, Long> {
    @Transactional
    @Modifying
    @Query(value = """
            delete from Choice c where c.question.id in :idQuestions
            """)
    void deleteAllChoiceByQuestionId(List<Long> idQuestions);

    @Modifying
    @Query(value = """
            delete from Choice c where c.question.id = :questionId
            """)
    void deleteChoiceByQuestionId(Long questionId);

    @Query(value = """
            select new com.gayuh.personalproject.dto.ChoiceResponse(
            c.id, c.choiceText, c.correct
            )
            from Choice c
            join Question q on c.question.id = q.id
            join QuestionTitle qt on q.questionTitle.id = qt.id
            where qt.id = :questionTitleId and q.id = :questionId
            """)
    List<ChoiceResponse> findAllChoiceByQuestionTitleIdAndQuestionId(String questionTitleId, Long questionId);

    @Query(value = """
            select c from Choice c
            join Question q on c.question.id = q.id
            join QuestionTitle qt on q.questionTitle.id = qt.id
            where c.id = :choiceId and q.id = :questionId and qt.id = :questionTitleId
            """)
    Optional<Choice> findChoiceByQuestionTitleIdAndQuestionIdAndChoiceId(String questionTitleId, Long questionId, Long choiceId);

    @Query(value = """
            select new com.gayuh.personalproject.query.ChoiceQuery(
            c.id,
            c.choiceText,
            c.correct,
            c.question.id
            )
            from Choice c where c.question.id = :questionId
            """)
    List<ChoiceQuery> findAllChoiceQueryByQuestionId(Long questionId);
}
