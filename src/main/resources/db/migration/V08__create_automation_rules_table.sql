CREATE TABLE IF NOT EXISTS automation_rules(
    automation_id       UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    name                VARCHAR(50)     NOT NULL,
    input_sensor_id     UUID,
    condition           VARCHAR(50)     NOT NULL,
    threshold_value     NUMERIC(15, 2)  NOT NULL,
    action_type         VARCHAR(50)     NOT NULL,
    target_sensor_id    UUID,
    is_active           BOOLEAN,

    CONSTRAINT fk_automation_input_sensor
        FOREIGN KEY (input_sensor_id)
        REFERENCES sensors(sensor_id),

    CONSTRAINT fk_automation_target_sensor
        FOREIGN KEY (target_sensor_id)
            REFERENCES sensors(sensor_id)
);