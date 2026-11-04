package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.enums.AlertStatus;
import henrique.igor.iiot.domain.repositories.AlertRepository;

import java.util.List;
import java.util.UUID;

public class ListAlertsUseCase {

    private final AlertRepository alertRepository;

    public ListAlertsUseCase(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public List<Alert> execute(UUID equipmentId, AlertStatus status) {
        if (equipmentId != null) {
            List<Alert> alerts = alertRepository.findByEquipmentId(equipmentId);
            if (status != null) {
                return alerts.stream()
                        .filter(alert -> alert.getStatus() == status)
                        .toList();
            }
            return alerts;
        }

        if (status != null) {
            return alertRepository.findByStatus(status);
        }

        return alertRepository.findAll();
    }
}
