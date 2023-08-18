-- Start test_histories

DROP TABLE IF EXISTS test_histories;

CREATE TABLE test_histories
(
    test_history_id   varchar(255) primary key,
    score             float        null,
    started_at        timestamp    not null,
    finished_at       timestamp    null,
    user_id           varchar(255) not null,
    question_title_id varchar(255) not null,
    constraint test_history_user_foreign_key foreign key (user_id) references users (user_id)
);

-- End test_histories

-- Start tests

DROP TABLE IF EXISTS tests;

CREATE table tests
(
    test_id         varchar(255) primary key,
    answer          int          null,
    question_id     BIGINT       not null,
    test_history_id varchar(255) not null,
    constraint test_question_foreign_key foreign key (question_id) references questions (question_id),
    constraint test_test_history_foreign_key foreign key (test_history_id) references test_histories (test_history_id)
);

-- End tests