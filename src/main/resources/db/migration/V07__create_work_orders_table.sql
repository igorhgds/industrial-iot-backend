CREATE TABLE IF NOT EXISTS work_orders(
    work_order_id       UUID        PRIMARY KEY     DEFAULT gen_random_uuid(),
    alert_id            UUID,
    equipment_id        UUID,
    assigned_to         UUID,
    title               VARCHAR(100)    NOT NULL,
    description         TEXT,
    priority            VARCHAR(50),
    status              VARCHAR(50)     NOT NULL,
    created_at          TIMESTAMPTZ     NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    closed_at           TIMESTAMPTZ,

    CONSTRAINT fk_work_order_alert
        FOREIGN KEY (alert_id)
        REFERENCES alerts(alert_id),

    CONSTRAINT fk_work_order_equipment
        FOREIGN KEY (equipment_id)
            REFERENCES equipments(equipment_id),

    CONSTRAINT fk_work_order_user
        FOREIGN KEY (assigned_to)
            REFERENCES users(user_id)
);