package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.repositories.SensorRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.SensorJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.SensorMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.SensorJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class SensorPersistenceAdapter implements SensorRepository {

    private final SensorJpaRepository sensorJpaRepository;

    public SensorPersistenceAdapter(SensorJpaRepository sensorJpaRepository) {
        this.sensorJpaRepository = sensorJpaRepository;
    }

    @Override
    public Sensor save(Sensor sensor) {
        SensorJpaEntity jpaEntity = SensorMapper.toJpaEntity(sensor);
        SensorJpaEntity savedEntity = sensorJpaRepository.save(jpaEntity);
        return SensorMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Sensor> findById(UUID sensorId) {
        return sensorJpaRepository.findById(sensorId)
                .map(SensorMapper::toDomain);
    }

    @Override
    public Optional<Sensor> findByCode(String code) {
        return sensorJpaRepository.findByCode(code)
                .map(SensorMapper::toDomain);
    }

    @Override
    public List<Sensor> findAll() {
        return sensorJpaRepository.findAll()
                .stream()
                .map(SensorMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID sensorId) {
        sensorJpaRepository.deleteById(sensorId);
    }
}
