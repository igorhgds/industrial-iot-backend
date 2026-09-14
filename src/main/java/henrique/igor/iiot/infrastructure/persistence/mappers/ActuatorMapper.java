package henrique.igor.iiot.infrastructure.persistence.mappers;

import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.infrastructure.persistence.entities.ActuatorJpaEntity;

public class ActuatorMapper {

    public static ActuatorJpaEntity toJpaEntity(Actuator domain){
        if (domain == null) return null;
        return new ActuatorJpaEntity(
                domain.getActuatorId(),
                domain.getCode(),
                domain.getActuatorType(),
                domain.getStatus(),
                domain.getCommandMqttTopic(),
                domain.getStateMqttTopic(),
                domain.getEquipment() != null ? EquipmentMapper.toJpaEntity(domain.getEquipment()) : null,
                domain.getCreatedAt()
        );
    }

    public static Actuator toDomain(ActuatorJpaEntity jpaEntity){
        if (jpaEntity == null) return null;
        return new Actuator(
                jpaEntity.getActuatorId(),
                jpaEntity.getCode(),
                jpaEntity.getActuatorType(),
                jpaEntity.getStatus(),
                jpaEntity.getCommandMqttTopic(),
                jpaEntity.getStateMqttTopic(),
                jpaEntity.getEquipment() != null ? EquipmentMapper.toDomain(jpaEntity.getEquipment()) : null,
                jpaEntity.getCreatedAt()
        );
    }
}
