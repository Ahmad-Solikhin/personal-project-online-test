DROP VIEW IF EXISTS VW_test_history;

CREATE VIEW VW_test_history as
select th.test_history_id,
       qt.question_title_id,
       qt.title,
       th.score,
       th.started_at,
       th.finished_at,
       extract(epoch from (th.finished_at - th.started_at)) as time,
       u.name,
       u.email
from test_histories th
         join users u on th.user_id = u.user_id
         join question_titles qt on th.question_title_id = qt.question_title_id;