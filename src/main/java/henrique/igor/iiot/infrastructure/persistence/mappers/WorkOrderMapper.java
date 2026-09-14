package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.WorkOrder;
import henrique.igor.iiot.infrastructure.persistence.entities.WorkOrderJpaEntity;

public class WorkOrderMapper {

    public static WorkOrderJpaEntity toJpaEntity(WorkOrder domain) {
        if (domain == null) return null;
        return new WorkOrderJpaEntity(
                domain.getWorkOrderId(),
                domain.getAlert() != null ? AlertMapper.toJpaEntity(domain.getAlert()) : null,
                domain.getEquipment() != null ? EquipmentMapper.toJpaEntity(domain.getEquipment()) : null,
                domain.getAssignedTo() != null ? UserMapper.toJpaEntity(domain.getAssignedTo()) : null,
                domain.getTitle(),
                domain.getDescription(),
                domain.getPriority(),
                domain.getStatus(),
                domain.getCreatedAt(),
                domain.getClosedAt()
        );
    }

    public static WorkOrder toDomain(WorkOrderJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new WorkOrder(
                jpaEntity.getWorkOrderId(),
                jpaEntity.getAlert() != null ? AlertMapper.toDomain(jpaEntity.getAlert()) : null,
                jpaEntity.getEquipment() != null ? EquipmentMapper.toDomain(jpaEntity.getEquipment()) : null,
                jpaEntity.getAssignedTo() != null ? UserMapper.toDomain(jpaEntity.getAssignedTo()) : null,
                jpaEntity.getTitle(),
                jpaEntity.getDescription(),
                jpaEntity.getPriority(),
                jpaEntity.getStatus(),
                jpaEntity.getCreatedAt(),
                jpaEntity.getClosedAt()
        );
    }
}
