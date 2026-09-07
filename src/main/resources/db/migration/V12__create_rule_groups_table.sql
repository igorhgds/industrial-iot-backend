CREATE TABLE IF NOT EXISTS rule_groups (
    rule_group_id   UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name            VARCHAR(100)    NOT NULL,
    description     TEXT,
    equipment_id    UUID            NOT NULL,
    severity        VARCHAR(30)     NOT NULL DEFAULT 'WARNING', -- INFO, WARNING, CRITICAL
    operator_type   VARCHAR(10)     NOT NULL DEFAULT 'AND',     -- AND, OR (para regras com múltiplas condições)
    is_active       BOOLEAN         NOT NULL DEFAULT TRUE,
    created_at      TIMESTAMPTZ     NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_rule_group_equipment
        FOREIGN KEY (equipment_id) REFERENCES equipments(equipment_id) ON DELETE CASCADE
);