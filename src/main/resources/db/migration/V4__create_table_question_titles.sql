DROP TABLE IF EXISTS question_titles;

CREATE TABLE question_titles
(
    question_title_id varchar(255) primary key,
    title             varchar(255) not null,
    token             varchar(255) null,
    started           boolean default false,
    created_at        timestamp    not null,
    updated_at        timestamp    not null,
    user_id           varchar(255) not null,
    topic_id          BIGINT       not null,
    difficulty_id     BIGINT       not null,
    access_id         bigint       not null,
    constraint question_title_user_foreign_key foreign key (user_id) references users (user_id),
    constraint question_title_topic_foreign_key foreign key (topic_id) references topics (topic_id),
    constraint question_title_difficulty_foreign_key foreign key (difficulty_id) references difficulties (difficulty_id),
    constraint question_title_access_foreign_key foreign key (access_id) references accesses (access_id)
);