ALTER TABLE tests
    RENAME COLUMN answer to choice_id;

ALTER TABLE tests
    ALTER COLUMN choice_id TYPE BIGINT USING null,
    ADD CONSTRAINT test_role_foreign_key FOREIGN KEY (choice_id) references choices (choice_id);
