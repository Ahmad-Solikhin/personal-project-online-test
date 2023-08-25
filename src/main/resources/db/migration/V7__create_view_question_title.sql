DROP VIEW IF EXISTS VW_question_title;

CREATE VIEW VW_question_title as
select qt.question_title_id,
       qt.title,
       qt.token,
       qt.started,
       qt.created_at,
       qt.updated_at,
       u.user_id,
       u.name                    as user_name,
       t.topic_id,
       t.name                    as topic_name,
       d.difficulty_id,
       d.name                    as difficulty_name,
       a.access_id,
       a.name                    as access_name,
       count(th.test_history_id) as tested
from question_titles qt
         join users u on qt.user_id = u.user_id
         join topics t on qt.topic_id = t.topic_id
         join accesses a on a.access_id = qt.access_id
         join difficulties d on d.difficulty_id = qt.difficulty_id
         left join test_histories th on qt.question_title_id = th.question_title_id
group by qt.question_title_id, t.topic_id, a.access_id, d.difficulty_id, th.test_history_id, u.user_id;

