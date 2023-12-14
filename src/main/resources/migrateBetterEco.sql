ALTER TABLE economy
    ADD COLUMN new_uuid_binary BINARY (16);

UPDATE economy
SET new_uuid_binary = UNHEX(REPLACE(uuid, '-', ''));


alter table economy
    drop column uuid;

alter table economy
    change new_uuid_binary uuid binary(16) null;

ALTER TABLE economy
    ADD PRIMARY KEY (uuid);

ALTER TABLE economy
    MODIFY balance DECIMAL (38, 2) DEFAULT 0 NOT NULL;

rename table economy to economy_player;
