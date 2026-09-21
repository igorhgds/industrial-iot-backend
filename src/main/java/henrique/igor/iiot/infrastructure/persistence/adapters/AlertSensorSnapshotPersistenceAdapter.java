package henrique.igor.iiot.infrastructure.persistence.adapters;

import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;
import henrique.igor.iiot.domain.repositories.AlertSensorSnapshotRepository;
import henrique.igor.iiot.infrastructure.persistence.entities.AlertSensorSnapshotJpaEntity;
import henrique.igor.iiot.infrastructure.persistence.mappers.AlertSensorSnapshotMapper;
import henrique.igor.iiot.infrastructure.persistence.repositories.jpa.AlertSensorSnapshotJpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class AlertSensorSnapshotPersistenceAdapter implements AlertSensorSnapshotRepository {

    private final AlertSensorSnapshotJpaRepository alertSensorSnapshotJpaRepository;

    public AlertSensorSnapshotPersistenceAdapter(AlertSensorSnapshotJpaRepository alertSensorSnapshotJpaRepository) {
        this.alertSensorSnapshotJpaRepository = alertSensorSnapshotJpaRepository;
    }

    @Override
    public AlertSensorSnapshot save(AlertSensorSnapshot snapshot) {
        AlertSensorSnapshotJpaEntity jpaEntity = AlertSensorSnapshotMapper.toJpaEntity(snapshot);
        AlertSensorSnapshotJpaEntity savedEntity = alertSensorSnapshotJpaRepository.save(jpaEntity);
        return AlertSensorSnapshotMapper.toDomain(savedEntity);
    }

    @Override
    public Optional<AlertSensorSnapshot> findById(UUID snapshotId) {
        return alertSensorSnapshotJpaRepository.findById(snapshotId)
                .map(AlertSensorSnapshotMapper::toDomain);
    }

    @Override
    public List<AlertSensorSnapshot> findByAlertId(UUID alertId) {
        return alertSensorSnapshotJpaRepository.findByAlertAlertId(alertId)
                .stream()
                .map(AlertSensorSnapshotMapper::toDomain)
                .toList();
    }

    @Override
    public List<AlertSensorSnapshot> findAll() {
        return alertSensorSnapshotJpaRepository.findAll()
                .stream()
                .map(AlertSensorSnapshotMapper::toDomain)
                .toList();
    }

    @Override
    public void deleteById(UUID snapshotId) {
        alertSensorSnapshotJpaRepository.deleteById(snapshotId);
    }
}
