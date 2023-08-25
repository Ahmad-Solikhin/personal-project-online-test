DROP VIEW IF EXISTS VW_question;

CREATE VIEW VW_question as
select q.question_id,
       qt.question_title_id,
       q.question,
       q.time,
       q.score,
       q.created_at,
       q.updated_at,
       m.media_id
from questions q
         join question_titles qt on q.question_title_id = qt.question_title_id
         left join medias m on q.question_id = m.question_id;