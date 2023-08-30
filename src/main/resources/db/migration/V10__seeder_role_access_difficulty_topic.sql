INSERT INTO roles(role_id, name) values (nextval('roles_sequence'), 'Admin'), (nextval('roles_sequence'), 'User'), (nextval('roles_sequence'), 'Guest');

INSERT INTO accesses(access_id, name) values (nextval('accesses_sequence'), 'Public'), (nextval('accesses_sequence'), 'Private');

INSERT INTO difficulties(difficulty_id, name) values (nextval('difficulties_sequence'), 'Easy'), (nextval('difficulties_sequence'), 'Medium'), (nextval('difficulties_sequence'), 'Hard');

INSERT INTO topics(topic_id, name) values (nextval('topics_sequence'), 'Programming'), (nextval('topics_sequence'), 'Science');