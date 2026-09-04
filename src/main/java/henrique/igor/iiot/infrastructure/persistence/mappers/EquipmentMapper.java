package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.infrastructure.persistence.entities.EquipmentJpaEntity;

public class EquipmentMapper {

    public static EquipmentJpaEntity toJpaEntity(Equipment domain){
        if (domain == null) return null;
        return new EquipmentJpaEntity(
                domain.getEquipmentId(),
                domain.getEquipCode(),
                domain.getType(),
                domain.getStatus(),
                domain.getSector() != null ? SectorMapper.toJpaEntity(domain.getSector()) : null,
                domain.getCreatedAt()
        );
    }

    public static Equipment toDomain(EquipmentJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new Equipment(
                jpaEntity.getEquipmentId(),
                jpaEntity.getEquipCode(),
                jpaEntity.getType(),
                jpaEntity.getStatus(),
                jpaEntity.getSector() != null ? SectorMapper.toDomain(jpaEntity.getSector()) : null,
                jpaEntity.getCreatedAt()
        );
    }
}
