CREATE TABLE IF NOT EXISTS alerts(
    alert_id            UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    equipment_id        UUID,
    sensor_id           UUID,
    severity            VARCHAR(50)     NOT NULL,
    message             TEXT            NOT NULL,
    value_at_trigger    NUMERIC(15, 2)  NOT NULL,
    status              VARCHAR(50)     NOT NULL,
    acknowledged_by     UUID,
    triggered_at        TIMESTAMPTZ     DEFAULT CURRENT_TIMESTAMP,
    resolved_at         TIMESTAMPTZ,

    CONSTRAINT fk_alert_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipments(equipment_id),

    CONSTRAINT fk_alert_sensor
        FOREIGN KEY (sensor_id)
        REFERENCES sensors(sensor_id),

    CONSTRAINT fk_alert_user
        FOREIGN KEY (acknowledged_by)
        REFERENCES users(user_id)
);