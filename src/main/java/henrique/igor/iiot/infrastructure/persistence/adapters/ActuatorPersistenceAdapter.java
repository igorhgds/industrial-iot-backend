package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.ActuatorJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.ActuatorMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.ActuatorJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class ActuatorPersistenceAdapter implements ActuatorRepository {

    private final ActuatorJpaRepository actuatorJpaRepository;

    public ActuatorPersistenceAdapter(ActuatorJpaRepository actuatorJpaRepository) {
        this.actuatorJpaRepository = actuatorJpaRepository;
    }

    @Override
    public Actuator save(Actuator actuator) {
        ActuatorJpaEntity jpaEntity = ActuatorMapper.toJpaEntity(actuator);
        ActuatorJpaEntity savedEntity = actuatorJpaRepository.save(jpaEntity);
        return ActuatorMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Actuator> findById(UUID actuatorId) {
        return actuatorJpaRepository.findById(actuatorId)
                .map(ActuatorMapper::toDomain);
    }

    @Override
    public Optional<Actuator> findByCode(String code) {
        return actuatorJpaRepository.findByCode(code)
                .map(ActuatorMapper::toDomain);
    }

    @Override
    public List<Actuator> findAll() {
        return actuatorJpaRepository.findAll()
                .stream()
                .map(ActuatorMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID actuatorId) {
        actuatorJpaRepository.deleteById(actuatorId);
    }
}
