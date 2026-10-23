package henrique.igor.iiot.application.usecases.alert;

import henrique.igor.iiot.application.usecases.alert.dto.AcknowledgeAlertInput;
import henrique.igor.iiot.domain.entities.Alert;
import henrique.igor.iiot.domain.entities.User;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.AlertRepository;
import henrique.igor.iiot.domain.repositories.UserRepository;

public class AcknowledgeAlertUseCase {

    private final AlertRepository alertRepository;
    private final UserRepository userRepository;

    public AcknowledgeAlertUseCase(AlertRepository alertRepository, UserRepository userRepository) {
        this.alertRepository = alertRepository;
        this.userRepository = userRepository;
    }

    public Alert execute(AcknowledgeAlertInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.alertId() == null) {
            throw new DomainValidationException("Alert ID cannot be null.");
        }
        if (input.userId() == null) {
            throw new DomainValidationException("User ID cannot be null.");
        }

        Alert alert = alertRepository.findById(input.alertId())
                .orElseThrow(() -> new ResourceNotFoundException("Alert", input.alertId()));

        User user = userRepository.findById(input.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User", input.userId()));

        alert.acknowledge(user);

        return alertRepository.save(alert);
    }
}
