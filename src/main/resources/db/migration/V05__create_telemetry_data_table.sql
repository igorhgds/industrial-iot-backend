CREATE TABLE IF NOT EXISTS telemetry_data(
    telemetry_id    UUID        PRIMARY KEY DEFAULT gen_random_uuid(),
    sensor_id       UUID        NOT NULL,
    timestamp       TIMESTAMPTZ NOT NULL    DEFAULT CURRENT_TIMESTAMP,
    value           NUMERIC(15, 2),
    raw_payload     TEXT,

    CONSTRAINT fk_telemetry_sensor
        FOREIGN KEY (sensor_id)
        REFERENCES sensors(sensor_id)
);