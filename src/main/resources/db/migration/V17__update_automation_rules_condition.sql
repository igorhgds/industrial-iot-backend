-- Alteração na tabela automation_rules para associar com a tabela rule_conditions via condition_id
ALTER TABLE automation_rules
    DROP COLUMN IF EXISTS condition;

ALTER TABLE automation_rules
    ADD COLUMN condition_id UUID,
    ADD CONSTRAINT fk_automation_rule_condition
        FOREIGN KEY (condition_id)
            REFERENCES rule_conditions(condition_id) ON DELETE SET NULL;
