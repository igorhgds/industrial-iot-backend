CREATE TABLE IF NOT EXISTS equipments(
    equipment_id    UUID            PRIMARY KEY  DEFAULT gen_random_uuid(),
    equip_code      VARCHAR(50)     NOT NULL UNIQUE,
    type            VARCHAR(50)     NOT NULL,
    status          VARCHAR(50)     NOT NULL,
    sector_id       UUID,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipment_sector
        FOREIGN KEY (sector_id)
        REFERENCES sectors(sector_id)
);