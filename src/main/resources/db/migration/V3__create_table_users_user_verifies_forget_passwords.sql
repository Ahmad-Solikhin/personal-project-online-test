-- Start users

DROP TABLE IF EXISTS users;

CREATE TABLE users
(
    user_id    varchar(255) primary key,
    name       varchar(255) not null,
    password   varchar(255) not null,
    email      varchar(255) not null unique,
    activated  boolean default false,
    suspend    boolean default false,
    created_at timestamp    not null,
    updated_at timestamp    not null,
    role_id    BIGINT       not null,
    constraint user_role_foreign_key foreign key (role_id) references roles (role_id)
);

-- End users

-- Start user verifies

DROP TABLE IF EXISTS user_verifies;

CREATE TABLE user_verifies
(
    user_verify_id bigint primary key,
    token          varchar(255)        not null,
    expired_at     timestamp           not null,
    user_id        varchar(255) unique not null,
    constraint user_verify_user_foreign_key foreign key (user_id) references users (user_id)
);

DROP SEQUENCE IF EXISTS user_verifies_sequence;

CREATE SEQUENCE user_verifies_sequence OWNED BY user_verifies.user_verify_id;

-- End user verifies

-- Start forget passwords

DROP TABLE IF EXISTS forget_passwords;

CREATE TABLE forget_passwords
(
    forget_password_id BIGINT primary key,
    token              varchar(255)        not null,
    expired_at         timestamp           not null,
    user_id            varchar(255) unique not null,
    constraint forget_password_user_foreign_key foreign key (user_id) references users (user_id)
);

DROP SEQUENCE IF EXISTS forget_passwords_sequence;

CREATE SEQUENCE forget_passwords_sequence OWNED BY forget_passwords.forget_password_id;

-- End forget passwords