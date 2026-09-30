package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.domain.entities.enums.SensorType;
import henrique.igor.iiot.infrastructure.persistence.entities.SensorJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface SensorJpaRepository extends JpaRepository<SensorJpaEntity, UUID> {

    Optional<SensorJpaEntity> findByCode(String code);

    long countByEquipment_EquipmentIdAndSensorType(UUID equipmentId, SensorType sensorType);

    long countByEquipmentIsNullAndSensorType(SensorType sensorType);

    List<SensorJpaEntity> findByEquipmentEquipmentId(UUID equipmentId);
}
