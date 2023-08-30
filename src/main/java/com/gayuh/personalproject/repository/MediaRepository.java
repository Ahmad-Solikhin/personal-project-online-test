package com.gayuh.personalproject.repository;

import com.gayuh.personalproject.entity.Media;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;

import java.util.List;

public interface MediaRepository extends JpaRepository<Media, String> {
    @Query(value = """
            select m from Media m
            join Question q on m.question.id = q.id
            join QuestionTitle qt on q.questionTitle.id = qt.id
            where qt.id = :questionTitleId
            """)
    List<Media> findAllMediaByQuestionTitleId(String questionTitleId);

    @Modifying
    @Query(value = """
            delete from Media m1
            where m1.id in
            (
                select m2.id from Media m2
                join Question q on m2.question.id = q.id
                join QuestionTitle qt on q.questionTitle.id = qt.id
                where qt.id = :questionTitleId
            )
            """)
    void deleteAllMediaByQuestionTitleId(String questionTitleId);
}
