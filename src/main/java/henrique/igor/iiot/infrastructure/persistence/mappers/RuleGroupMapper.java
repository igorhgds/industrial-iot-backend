package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.infrastructure.persistence.entities.RuleGroupJpaEntity;

public class RuleGroupMapper {

    public static RuleGroupJpaEntity toJpaEntity(RuleGroup domain){
        if (domain == null) return null;
        return new RuleGroupJpaEntity(
                domain.getRuleGroupId(),
                domain.getName(),
                domain.getDescription(),
                domain.getEquipment() != null ? EquipmentMapper.toJpaEntity(domain.getEquipment()) : null,
                domain.getSeverity(),
                domain.getOperatorType(),
                domain.getActive(),
                domain.getCreatedAt()
        );
    }

    public static RuleGroup toDomain(RuleGroupJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new RuleGroup(
                jpaEntity.getRuleGroupId(),
                jpaEntity.getName(),
                jpaEntity.getDescription(),
                jpaEntity.getEquipment() != null ? EquipmentMapper.toDomain(jpaEntity.getEquipment()) : null,
                jpaEntity.getSeverity(),
                jpaEntity.getOperatorType(),
                jpaEntity.getIsActive(),
                jpaEntity.getCreatedAt()
        );
    }
}
