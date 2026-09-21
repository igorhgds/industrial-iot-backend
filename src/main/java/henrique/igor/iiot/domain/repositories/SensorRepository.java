package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Sensor;

import java.util.*;

public interface SensorRepository {

    Sensor save(Sensor sensor);

    Optional<Sensor> findById(UUID sensorId);

    Optional<Sensor> findByCode(String code);

    List<Sensor> findAll();

    void deleteById(UUID sensorId);
}
