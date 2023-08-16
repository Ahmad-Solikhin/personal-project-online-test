DROP TABLE IF EXISTS roles;

CREATE TABLE roles(
  role_id BIGINT primary key,
  name VARCHAR(100)
);

DROP SEQUENCE IF EXISTS roles_sequence;

CREATE SEQUENCE roles_sequence OWNED BY roles.role_id;