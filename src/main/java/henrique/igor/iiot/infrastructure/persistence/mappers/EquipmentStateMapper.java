package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.EquipmentState;
import henrique.igor.iiot.infrastructure.persistence.entities.EquipmentStateJpaEntity;

public class EquipmentStateMapper {

    public static EquipmentStateJpaEntity toJpaEntity(EquipmentState domain){
        if (domain == null) return null;
        return new EquipmentStateJpaEntity(
                domain.getEquipmentId() != null ? EquipmentMapper.toJpaEntity(domain.getEquipmentId()) : null,
                domain.getStatus(),
                domain.getLastPayload(),
                domain.getUpdatedAt()
        );
    }

    public static EquipmentState toDomain(EquipmentStateJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new EquipmentState(
                jpaEntity.getEquipmentId() != null ? EquipmentMapper.toDomain(jpaEntity.getEquipment()) : null,
                jpaEntity.getStatus(),
                jpaEntity.getLastPayload(),
                jpaEntity.getUpdatedAt()
        );
    }
}
