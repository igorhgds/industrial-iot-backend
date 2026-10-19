package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.ActuatorJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.ActuatorMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.ActuatorJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Repository
public class ActuatorPersistenceAdapter implements ActuatorRepository {

    private static final Pattern CODE_SEQUENCE_PATTERN = Pattern.compile("^[A-Z0-9]+-(\\d+)-[A-Z0-9]+$");

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

    @Override
    public Optional<Long> findMaxSequenceByEquipmentIdAndType(UUID equipmentId, ActuatorType actuatorType) {
        List<String> codes = actuatorJpaRepository.findCodesByEquipmentIdAndType(equipmentId, actuatorType);
        return extractMaxSequence(codes);
    }

    @Override
    public Optional<Long> findMaxSequenceForUnassignedEquipment(ActuatorType actuatorType) {
        List<String> codes = actuatorJpaRepository.findCodesByEquipmentIsNullAndType(actuatorType);
        return extractMaxSequence(codes);
    }

    private Optional<Long> extractMaxSequence(List<String> codes) {
        return codes.stream()
                .map(CODE_SEQUENCE_PATTERN::matcher)
                .filter(Matcher::matches)
                .map(matcher -> Long.parseLong(matcher.group(1)))
                .max(Long::compareTo);
    }
}