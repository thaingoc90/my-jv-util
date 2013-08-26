DROP TABLE IF EXISTS nhp_paperclip;

CREATE TABLE nhp_paperclip (
    pcid                    VARCHAR(64)             NOT NULL,
    pcoriginal_name         VARCHAR(64),             
    pccreate_time           DATETIME,
        INDEX (pccreate_time),
    pcfilesize              INT                     NOT NULL DEFAULT 0,
    pcfilestatus            VARCHAR(32),
        INDEX (pcfilestatus),
    pcmimetype              VARCHAR(128)            NOT NULL DEFAULT '',
        INDEX (pcmimetype),
    pcowner                 VARCHAR(32),
        INDEX (pcowner),
    pcmetadata              TEXT,
    PRIMARY KEY (pcid)
) ENGINE=InnoDB DEFAULT CHARACTER SET utf8 COLLATE utf8_unicode_ci;
