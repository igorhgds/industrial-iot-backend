package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.infrastructure.persistence.entities.RuleConditionJpaEntity;

public class RuleConditionMapper {

    public static RuleConditionJpaEntity toJpaEntity(RuleCondition domain){
        if (domain == null) return null;
        return new RuleConditionJpaEntity(
                domain.getConditionId(),
                domain.getRuleGroup() != null ? RuleGroupMapper.toJpaEntity(domain.getRuleGroup()) : null,
                domain.getSensor() != null ? SensorMapper.toJpaEntity(domain.getSensor()) : null,
                domain.getOperator(),
                domain.getThresholdValue()
        );
    }

    public static RuleCondition toDomain(RuleConditionJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new RuleCondition(
                jpaEntity.getConditionId(),
                jpaEntity.getRuleGroup() != null ? RuleGroupMapper.toDomain(jpaEntity.getRuleGroup()) : null,
                jpaEntity.getSensor() != null ? SensorMapper.toDomain(jpaEntity.getSensor()) : null,
                jpaEntity.getOperator(),
                jpaEntity.getThresholdValue()
        );
    }
}
