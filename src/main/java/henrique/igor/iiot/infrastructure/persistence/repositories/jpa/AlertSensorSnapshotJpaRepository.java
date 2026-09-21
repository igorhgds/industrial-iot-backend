package henrique.igor.iiot.infrastructure.persistence.repositories.jpa;

import henrique.igor.iiot.infrastructure.persistence.entities.AlertSensorSnapshotJpaEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface AlertSensorSnapshotJpaRepository extends JpaRepository<AlertSensorSnapshotJpaEntity, UUID> {

    List<AlertSensorSnapshotJpaEntity> findByAlertAlertId(UUID alertId);
}
