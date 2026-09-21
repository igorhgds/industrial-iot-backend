package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertSensorSnapshotRepository {

    AlertSensorSnapshot save(AlertSensorSnapshot snapshot);

    Optional<AlertSensorSnapshot> findById(UUID snapshotId);

    List<AlertSensorSnapshot> findByAlertId(UUID alertId);

    List<AlertSensorSnapshot> findAll();

    void deleteById(UUID snapshotId);
}
