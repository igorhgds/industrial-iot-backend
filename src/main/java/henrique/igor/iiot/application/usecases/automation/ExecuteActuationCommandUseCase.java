package henrique.igor.iiot.application.usecases.automation;

import henrique.igor.iiot.application.usecases.automation.dto.ActuationCommandResult;
import henrique.igor.iiot.application.usecases.automation.dto.ExecuteActuationCommandInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.ports.ActuationCommandPublisher;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;

import java.time.OffsetDateTime;

public class ExecuteActuationCommandUseCase {

    private final AutomationRuleRepository automationRuleRepository;
    private final ActuationCommandPublisher commandPublisher;

    public ExecuteActuationCommandUseCase(
            AutomationRuleRepository automationRuleRepository,
            ActuationCommandPublisher commandPublisher
    ) {
        this.automationRuleRepository = automationRuleRepository;
        this.commandPublisher = commandPublisher;
    }

    public ActuationCommandResult execute(ExecuteActuationCommandInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.automationRuleId() == null) {
            throw new DomainValidationException("AutomationRule ID cannot be null.");
        }

        AutomationRule rule = automationRuleRepository.findById(input.automationRuleId())
                .orElseThrow(() -> new ResourceNotFoundException("AutomationRule", input.automationRuleId()));

        if (!Boolean.TRUE.equals(rule.getActive())) {
            throw new DomainValidationException("Cannot execute actuation command for an inactive AutomationRule.");
        }

        Actuator actuator = rule.getTargetActuator();
        if (actuator == null) {
            throw new DomainValidationException("Target actuator is not configured for this AutomationRule.");
        }
        if (actuator.getStatus() != ActuatorStatus.ONLINE) {
            throw new DomainValidationException("Target actuator must be ONLINE to receive commands.");
        }
        if (actuator.getCommandMqttTopic() == null || actuator.getCommandMqttTopic().isBlank()) {
            throw new DomainValidationException("Target actuator does not have a valid command MQTT topic.");
        }

        String payload = (input.commandPayload() != null && !input.commandPayload().isBlank())
                ? input.commandPayload()
                : rule.getActionType().name();

        commandPublisher.publishCommand(actuator.getCommandMqttTopic(), payload);

        return new ActuationCommandResult(
                rule.getAutomationId(),
                actuator.getCode(),
                actuator.getCommandMqttTopic(),
                rule.getActionType(),
                payload,
                OffsetDateTime.now()
        );
    }
}
