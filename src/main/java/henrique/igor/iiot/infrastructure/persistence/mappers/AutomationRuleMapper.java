package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.infrastructure.persistence.entities.AutomationRuleJpaEntity;

public class AutomationRuleMapper {

    public static AutomationRuleJpaEntity toJpaEntity(AutomationRule domain) {
        if (domain == null) return null;
        return new AutomationRuleJpaEntity(
                domain.getAutomationId(),
                domain.getName(),
                SensorMapper.toJpaEntity(domain.getInputSensorId()),
                RuleConditionMapper.toJpaEntity(domain.getCondition()),
                domain.getThresholdValue(),
                domain.getActionType(),
                ActuatorMapper.toJpaEntity(domain.getTargetActuator()),
                domain.getActive()
        );
    }

    public static AutomationRule toDomain(AutomationRuleJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new AutomationRule(
                jpaEntity.getAutomationId(),
                jpaEntity.getName(),
                SensorMapper.toDomain(jpaEntity.getInputSensor()),
                RuleConditionMapper.toDomain(jpaEntity.getCondition()),
                jpaEntity.getThresholdValue(),
                jpaEntity.getActionType(),
                ActuatorMapper.toDomain(jpaEntity.getTargetActuator()),
                jpaEntity.getIsActive()
        );
    }
}
