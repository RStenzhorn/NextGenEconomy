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

alter table economy
    drop column uuid;

alter table economy
    change new_uuid_binary uuid binary(16) null;

ALTER TABLE economy
    MODIFY balance DECIMAL (38, 2) DEFAULT 0 NOT NULL;

ALTER TABLE economy
    ADD PRIMARY KEY (uuid);

rename table economy to economy_player;
