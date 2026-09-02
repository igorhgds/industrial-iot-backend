package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.infrastructure.persistence.entities.SensorJpaEntity;

public class SensorMapper {

    public static SensorJpaEntity toJpaEntity(Sensor domain){
        if (domain == null) return null;
        return new SensorJpaEntity(
                domain.getSensorId(),
                domain.getCode(),
                domain.getType(),
                domain.getStatus(),
                domain.getUnitOfMeasure(),
                domain.getMinThreshold(),
                domain.getMaxThreshold(),
                domain.getMqttTopic(),
                domain.getEquipment() != null ? EquipmentMapper.toJpaEntity(domain.getEquipment()): null,
                domain.getCreatedAt()
        );
    }

    public static Sensor toDomain(SensorJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new Sensor(
                jpaEntity.getSensorId(),
                jpaEntity.getCode(),
                jpaEntity.getType(),
                jpaEntity.getStatus(),
                jpaEntity.getUnitOfMeasure(),
                jpaEntity.getMinThreshold(),
                jpaEntity.getMaxThreshold(),
                jpaEntity.getMqttTopic(),
                jpaEntity.getEquipment() != null ? EquipmentMapper.toDomain(jpaEntity.getEquipment()) : null,
                jpaEntity.getCreatedAt()
        );
    }
}
