CREATE TABLE IF NOT EXISTS sensors(
    sensor_id       UUID    PRIMARY KEY     DEFAULT gen_random_uuid(),
    equipment_id    UUID,
    code            VARCHAR(50)     NOT NULL,
    sensor_type     VARCHAR(50)     NOT NULL,
    unit_of_measure VARCHAR(5),
    min_threshold   NUMERIC(15, 2),
    max_threshold   NUMERIC(15, 2),
    status          VARCHAR(50),
    mqtt_topic      VARCHAR(100),
    created_at      TIMESTAMPTZ     NOT NULL    DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_sensor_equipment
        FOREIGN KEY (equipment_id)
        REFERENCES equipments(equipment_id)
);