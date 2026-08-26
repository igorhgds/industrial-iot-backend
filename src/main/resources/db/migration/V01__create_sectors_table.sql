CREATE TABLE IF NOT EXISTS sectors (
    sector_id       UUID            PRIMARY KEY     DEFAULT gen_random_uuid(),
    name            VARCHAR(50)     NOT NULL,
    description     TEXT,
    created_at      TIMESTAMPTZ     NOT NULL    DEFAULT CURRENT_TIMESTAMP
);