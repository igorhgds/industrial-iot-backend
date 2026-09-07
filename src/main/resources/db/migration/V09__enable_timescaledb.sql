-- 1. Enable extension
CREATE EXTENSION IF NOT EXISTS timescaledb;

-- 2. Remove PK simples que bloqueia a criação da hypertable
ALTER TABLE telemetry_data DROP CONSTRAINT IF EXISTS telemetry_data_pkey;

-- 3. Convert raw_payload de TEXT para JSONB para queries flexíveis
ALTER TABLE telemetry_data
    ALTER COLUMN raw_payload TYPE JSONB USING raw_payload::jsonb;

-- 4. Convert telemetry_data em Hypertable (migrate_data => TRUE preserva dados existentes)
SELECT create_hypertable('telemetry_data', 'timestamp', migrate_data => TRUE, if_not_exists => TRUE);

-- 5. Index otimizado para a query mais comum: "filtrar por sensor ordenado por tempo"
CREATE INDEX IF NOT EXISTS idx_telemetry_sensor_timestamp
    ON telemetry_data (sensor_id, timestamp DESC);

-- 6. Retention Policy (exemplo: purga automática de dados brutos com mais de 90 dias)
SELECT add_retention_policy('telemetry_data', INTERVAL '90 days', if_not_exists => TRUE);