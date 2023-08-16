-- Start Topics
DROP TABLE IF EXISTS topics;

CREATE TABLE topics
(
    topic_id BIGINT primary key,
    name     varchar(100)
);

DROP SEQUENCE IF EXISTS topics_sequence;

CREATE SEQUENCE topics_sequence owned by topics.topic_id;

-- End Topics

-- Start Difficulties

DROP TABLE IF EXISTS difficulties;

CREATE TABLE difficulties
(
    difficulty_id BIGINT primary key,
    name          varchar(100)
);

DROP SEQUENCE IF EXISTS difficulties_sequence;

CREATE SEQUENCE difficulties_sequence owned by difficulties.difficulty_id;

-- End Difficulties

-- Start Accesses

DROP TABLE IF EXISTS accesses;

CREATE TABLE accesses
(
    access_id BIGINT primary key,
    name     varchar(100)
);

DROP SEQUENCE IF EXISTS accesses_sequence;

CREATE SEQUENCE accesses_sequence owned by accesses.access_id;

-- End Accesses