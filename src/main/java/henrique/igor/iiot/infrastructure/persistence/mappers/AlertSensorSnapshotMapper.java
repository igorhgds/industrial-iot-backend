package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertSensorSnapshotJpaEntity;

public class AlertSensorSnapshotMapper {

    public static AlertSensorSnapshotJpaEntity toJpaEntity(AlertSensorSnapshot domain, AlertJpaEntity parent){
        if (domain == null) return null;
        return new AlertSensorSnapshotJpaEntity(
                domain.getSnapshotId(),
                parent,
                domain.getSensor() != null ? SensorMapper.toJpaEntity(domain.getSensor()) : null,
                domain.getValueAtTrigger()
        );
    }

    public static AlertSensorSnapshot toDomain(AlertSensorSnapshotJpaEntity jpaEntity) {
        if (jpaEntity == null) return null;
        return new AlertSensorSnapshot(
                jpaEntity.getSnapshotId(),
                jpaEntity.getSensor() != null ? SensorMapper.toDomain(jpaEntity.getSensor()) : null,
                jpaEntity.getValueAtTrigger()
        );
    }
}
