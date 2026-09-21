package henrique.igor.iiot.domain.repositories;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface AlertRepository {

    Alert save(Alert alert);

    Optional<Alert> findById(UUID alertId);

    List<Alert> findByEquipmentId(UUID equipmentId);

    List<Alert> findByStatus(AlertStatus status);

    List<Alert> findAll();

    void deleteById(UUID alertId);
}
