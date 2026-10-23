package henrique.igor.iiot.application.usecases.automation;

import henrique.igor.iiot.application.usecases.automation.dto.CreateAutomationRuleInput;
import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.ActuatorRepository;
import henrique.igor.iiot.domain.repositories.AutomationRuleRepository;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;

public class CreateAutomationRuleUseCase {

    private final AutomationRuleRepository automationRuleRepository;
    private final SensorRepository sensorRepository;
    private final RuleConditionRepository ruleConditionRepository;
    private final ActuatorRepository actuatorRepository;

    public CreateAutomationRuleUseCase(
            AutomationRuleRepository automationRuleRepository,
            SensorRepository sensorRepository,
            RuleConditionRepository ruleConditionRepository,
            ActuatorRepository actuatorRepository
    ) {
        this.automationRuleRepository = automationRuleRepository;
        this.sensorRepository = sensorRepository;
        this.ruleConditionRepository = ruleConditionRepository;
        this.actuatorRepository = actuatorRepository;
    }

    public AutomationRule execute(CreateAutomationRuleInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.name() == null || input.name().isBlank()) {
            throw new DomainValidationException("Automation rule name cannot be null or blank.");
        }
        if (input.inputSensorId() == null) {
            throw new DomainValidationException("Input sensor ID cannot be null.");
        }
        if (input.conditionId() == null) {
            throw new DomainValidationException("Condition ID cannot be null.");
        }
        if (input.thresholdValue() == null) {
            throw new DomainValidationException("Threshold value cannot be null.");
        }
        if (input.actionType() == null) {
            throw new DomainValidationException("Action type cannot be null.");
        }
        if (input.targetActuatorId() == null) {
            throw new DomainValidationException("Target actuator ID cannot be null.");
        }

        Sensor sensor = sensorRepository.findById(input.inputSensorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", input.inputSensorId()));

        RuleCondition condition = ruleConditionRepository.findById(input.conditionId())
                .orElseThrow(() -> new ResourceNotFoundException("RuleCondition", input.conditionId()));

        Actuator actuator = actuatorRepository.findById(input.targetActuatorId())
                .orElseThrow(() -> new ResourceNotFoundException("Actuator", input.targetActuatorId()));

        if (condition.getSensor() != null && !condition.getSensor().getSensorId().equals(sensor.getSensorId())) {
            throw new DomainValidationException("RuleCondition does not belong to the specified input sensor.");
        }

        boolean active = Boolean.TRUE.equals(input.isActive());

        AutomationRule automationRule = new AutomationRule(
                input.name(),
                sensor,
                condition,
                input.thresholdValue(),
                input.actionType(),
                actuator,
                active
        );

        return automationRuleRepository.save(automationRule);
    }
}
