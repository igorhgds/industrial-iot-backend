package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertSensorSnapshotJpaEntity;

import java.util.List;

public class AlertMapper {

    public static AlertJpaEntity toJpaEntity(Alert domain){
        if (domain == null) return null;

        AlertJpaEntity jpaEntity = new AlertJpaEntity(
                domain.getAlertId(),
                domain.getEquipment() != null ? EquipmentMapper.toJpaEntity(domain.getEquipment()) : null,
                domain.getRuleGroup() != null ? RuleGroupMapper.toJpaEntity(domain.getRuleGroup()) : null,
                domain.getSeverity(),
                domain.getMessage(),
                domain.getStatus(),
                domain.getAcknowledgedBy() != null ? UserMapper.toJpaEntity(domain.getAcknowledgedBy()) : null,
                domain.getTriggeredAt(),
                domain.getResolvedAt(),
                null
        );

        List<AlertSensorSnapshotJpaEntity> snapshots = domain.getSensorSnapshots() != null
                ? domain.getSensorSnapshots()
                .stream()
                .map(snapshot -> AlertSensorSnapshotMapper.toJpaEntity(snapshot, jpaEntity))
                .toList()
                : List.of();

        jpaEntity.setSensorSnapshots(snapshots);
        return jpaEntity;
    }

    public static Alert toDomain(AlertJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new Alert(
                jpaEntity.getAlertId(),
                jpaEntity.getEquipment() != null ? EquipmentMapper.toDomain(jpaEntity.getEquipment()) : null,
                jpaEntity.getRuleGroup() != null ? RuleGroupMapper.toDomain(jpaEntity.getRuleGroup()) : null,
                jpaEntity.getSeverity(),
                jpaEntity.getMessage(),
                jpaEntity.getStatus(),
                jpaEntity.getAcknowledgedBy() != null ? UserMapper.toDomain(jpaEntity.getAcknowledgedBy()) : null,
                jpaEntity.getTriggeredAt(),
                jpaEntity.getResolvedAt(),
                jpaEntity.getSensorSnapshots() != null
                        ? jpaEntity.getSensorSnapshots()
                        .stream()
                        .map(AlertSensorSnapshotMapper::toDomain)
                        .toList()
                        : List.of()
        );
    }
}
