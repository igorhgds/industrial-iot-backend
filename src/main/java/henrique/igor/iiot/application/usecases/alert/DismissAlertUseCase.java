package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;

import java.util.UUID;

public class DismissAlertUseCase {

    private final AlertRepository alertRepository;

    public DismissAlertUseCase(AlertRepository alertRepository) {
        this.alertRepository = alertRepository;
    }

    public Alert execute(UUID alertId) {
        if (alertId == null) {
            throw new DomainValidationException("Alert ID cannot be null.");
        }

        Alert alert = alertRepository.findById(alertId)
                .orElseThrow(() -> new ResourceNotFoundException("Alert", alertId));

        alert.dismiss();

        return alertRepository.save(alert);
    }
}
