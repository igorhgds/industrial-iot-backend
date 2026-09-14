-- 1. Remove colunas de sensor único e valor único do alerta antigo
ALTER TABLE alerts DROP CONSTRAINT IF EXISTS fk_alert_sensor;
ALTER TABLE alerts DROP COLUMN IF EXISTS sensor_id;
ALTER TABLE alerts DROP COLUMN IF EXISTS value_at_trigger;

-- 2. Adiciona FK para rule_groups (qual regra disparou o alerta)
ALTER TABLE alerts ADD COLUMN rule_group_id UUID;

ALTER TABLE alerts ADD CONSTRAINT fk_alert_rule_group
    FOREIGN KEY (rule_group_id) REFERENCES rule_groups(rule_group_id);

-- 3. Tabela de snapshot multi-sensor: registra o valor de cada sensor no momento do disparo
CREATE TABLE IF NOT EXISTS alert_sensor_snapshots (
    snapshot_id         UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    alert_id            UUID            NOT NULL,
    sensor_id           UUID            NOT NULL,
    value_at_trigger    NUMERIC(15, 2)  NOT NULL,

    CONSTRAINT fk_snapshot_alert
        FOREIGN KEY (alert_id)
        REFERENCES alerts(alert_id) ON DELETE CASCADE,

    CONSTRAINT fk_snapshot_sensor
        FOREIGN KEY (sensor_id)
        REFERENCES sensors(sensor_id)
);

-- 4. Index para buscar snapshots por alerta de forma eficiente
CREATE INDEX IF NOT EXISTS idx_alert_sensor_snapshots_alert
    ON alert_sensor_snapshots (alert_id);
