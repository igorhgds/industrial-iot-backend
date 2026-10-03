package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;

import java.util.*;

public interface ActuatorRepository {

    Actuator save(Actuator actuator);

    Optional<Actuator> findById(UUID actuatorId);

    Optional<Actuator> findByCode(String code);

    List<Actuator> findAll();

    void deleteById(UUID actuatorId);

    long countByEquipmentIdAndActuatorType(UUID equipmentId, ActuatorType actuatorType);

    long countByCountEquipementIdIsNullAndActuatorType(ActuatorType actuatorType);
}
