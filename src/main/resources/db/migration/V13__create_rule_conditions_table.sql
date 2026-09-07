CREATE TABLE IF NOT EXISTS rule_conditions (
    condition_id    UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    rule_group_id   UUID            NOT NULL,
    sensor_id       UUID            NOT NULL,
    operator        VARCHAR(30)     NOT NULL, -- GREATER_THAN, LESS_THAN, EQUALS, GREATER_THAN_OR_EQUAL, LESS_THAN_OR_EQUAL
    threshold_value NUMERIC(15, 2)  NOT NULL,

    CONSTRAINT fk_rule_condition_group
        FOREIGN KEY (rule_group_id) REFERENCES rule_groups(rule_group_id) ON DELETE CASCADE,

    CONSTRAINT fk_rule_condition_sensor
        FOREIGN KEY (sensor_id) REFERENCES sensors(sensor_id) ON DELETE CASCADE
);