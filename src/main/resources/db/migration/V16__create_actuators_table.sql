-- 1. Criação da tabela de Atuadores (Actuators)
CREATE TABLE IF NOT EXISTS actuators (
    actuator_id         UUID            PRIMARY KEY DEFAULT gen_random_uuid(),
    code                VARCHAR(50)     NOT NULL UNIQUE,
    actuator_type       VARCHAR(50)     NOT NULL,
    status              VARCHAR(50)     NOT NULL,
    command_mqtt_topic  VARCHAR(255),
    state_mqtt_topic    VARCHAR(255),
    equipment_id        UUID,
    created_at          TIMESTAMP WITH TIME ZONE NOT NULL DEFAULT CURRENT_TIMESTAMP,

    CONSTRAINT fk_actuator_equipment
        FOREIGN KEY (equipment_id)
            REFERENCES equipments(equipment_id)
);

-- 2. Alteração na tabela automation_rules para apontar para o novo Actuator
ALTER TABLE automation_rules
    DROP CONSTRAINT IF EXISTS fk_automation_target_sensor,
    DROP COLUMN IF EXISTS target_sensor_id;

ALTER TABLE automation_rules
    ADD COLUMN target_actuator_id UUID,
    ADD CONSTRAINT fk_automation_target_actuator
        FOREIGN KEY (target_actuator_id)
            REFERENCES actuators(actuator_id);