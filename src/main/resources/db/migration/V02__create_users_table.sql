CREATE TABLE IF NOT EXISTS users(
    user_id             UUID             PRIMARY KEY    DEFAULT gen_random_uuid(),
    name                VARCHAR(50)      NOT NULL,
    email               VARCHAR(100)     NOT NULL UNIQUE,
    password            VARCHAR(255)     NOT NULL,
    password_recovery   VARCHAR(8),
    user_role           VARCHAR(30),
    sector_id           UUID,
    created_at          TIMESTAMPTZ     NOT NULL    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_user_sector
        FOREIGN KEY (sector_id)
        REFERENCES sectors(sector_id)
);