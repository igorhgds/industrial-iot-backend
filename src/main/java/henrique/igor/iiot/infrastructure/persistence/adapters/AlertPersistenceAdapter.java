package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.AlertMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.AlertJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AlertPersistenceAdapter implements AlertRepository {

    private final AlertJpaRepository alertJpaRepository;

    public AlertPersistenceAdapter(AlertJpaRepository alertJpaRepository) {
        this.alertJpaRepository = alertJpaRepository;
    }

    @Override
    public Alert save(Alert alert) {
        AlertJpaEntity jpaEntity = AlertMapper.toJpaEntity(alert);
        AlertJpaEntity savedEntity = alertJpaRepository.save(jpaEntity);
        return AlertMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<Alert> findById(UUID alertId) {
        return alertJpaRepository.findById(alertId)
                .map(AlertMapper::toDomain);
    }

    @Override
    public List<Alert> findByEquipmentId(UUID equipmentId) {
        return alertJpaRepository.findByEquipmentEquipmentId(equipmentId)
                .stream()
                .map(AlertMapper::toDomain)
                .toList();
    }

    @Override
    public List<Alert> findByStatus(AlertStatus status) {
        return alertJpaRepository.findByStatus(status)
                .stream()
                .map(AlertMapper::toDomain)
                .toList();
    }

    @Override
    public List<Alert> findAll() {
        return alertJpaRepository.findAll()
                .stream()
                .map(AlertMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID alertId) {
        alertJpaRepository.deleteById(alertId);
    }
}
