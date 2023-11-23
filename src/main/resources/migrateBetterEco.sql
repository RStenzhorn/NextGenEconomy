ALTER TABLE economy
    ADD COLUMN new_uuid_binary BINARY (16);

UPDATE economy
SET new_uuid_binary = UNHEX(REPLACE(uuid, '-', ''));

ALTER TABLE economyALTER TABLE economy
    ADD COLUMN new_uuid_binary BINARY (16);

UPDATE economy
SET new_uuid_binary = UNHEX(REPLACE(uuid, '-', ''));

ALTER TABLE economy
    DROP COLUMN uuid,
    CHANGE COLUMN new_uuid_binary uuid BINARY (16);

ALTER TABLE economy
    MODIFY balance DECIMAL (38, 2) DEFAULT 0 NOT NULL;

ALTER TABLE economy
    ADD PRIMARY KEY (uuid);

DROP
COLUMN uuid,
    CHANGE COLUMN new_uuid_binary uuid BINARY(16);

ALTER TABLE economy
    MODIFY balance DECIMAL (38, 2) DEFAULT 0 NOT NULL;

ALTER TABLE economy
    ADD PRIMARY KEY (uuid);
