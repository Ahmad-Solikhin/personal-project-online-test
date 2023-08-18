-- Start questions

DROP TABLE IF EXISTS questions;

CREATE TABLE questions
(
    question_id       bigint primary key,
    question          text         not null,
    time              int          not null,
    score             int          not null,
    created_at        timestamp    not null,
    updated_at        timestamp    not null,
    question_title_id varchar(255) not null,
    constraint question_question_title_foreign_key foreign key (question_title_id) references question_titles (question_title_id)
);

DROP SEQUENCE IF EXISTS questions_sequence;

CREATE SEQUENCE questions_sequence owned by questions.question_id;

-- End questions

-- Start choices

DROP TABLE IF EXISTS choices;

CREATE TABLE choices
(
    choice_id   BIGINT primary key,
    choice      varchar(255) not null,
    correct     boolean default false,
    question_id bigint       not null,
    constraint choice_question_foreign_key foreign key (question_id) references questions (question_id)
);

DROP SEQUENCE IF EXISTS choices_sequence;

CREATE SEQUENCE choices_sequence owned by choices.choice_id;

-- End choices

-- Start medias

DROP TABLE IF EXISTS medias;
CREATE TABLE medias
(
    media_id    varchar(255) primary key,
    name        varchar(255) not null,
    type        varchar(100) not null,
    size        int          not null,
    question_id bigint       not null unique,
    constraint media_question_foreign_key foreign key (question_id) references questions (question_id)
);

-- End medias