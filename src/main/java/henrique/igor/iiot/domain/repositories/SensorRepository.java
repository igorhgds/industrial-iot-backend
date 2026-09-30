package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.SensorType;

import java.util.*;

public interface SensorRepository {

    Sensor save(Sensor sensor);

    Optional<Sensor> findById(UUID sensorId);

    Optional<Sensor> findByCode(String code);

    List<Sensor> findAll();

    List<Sensor> findByEquipmentId(UUID equipmentId);

    void deleteById(UUID sensorId);

    long countByEquipmentIdAndSensorType(UUID equipmentId, SensorType sensorType);

    long countByEquipmentIdIsNullAndSensorType(SensorType sensorType);
}
