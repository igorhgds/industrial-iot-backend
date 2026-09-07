CREATE TABLE IF NOT EXISTS equipment_state (
    equipment_id  UUID        PRIMARY KEY,
    status        VARCHAR(50) NOT NULL DEFAULT 'UNKNOWN',
    last_payload  JSONB       NOT NULL DEFAULT '{}'::jsonb,
    updated_at    TIMESTAMPTZ NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_equipment_state_equipment
        FOREIGN KEY (equipment_id) REFERENCES equipments(equipment_id) ON DELETE CASCADE
);