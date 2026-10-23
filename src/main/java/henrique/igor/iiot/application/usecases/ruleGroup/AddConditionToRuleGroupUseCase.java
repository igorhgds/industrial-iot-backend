package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.AddConditionToRuleGroupInput;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;
import henrique.igor.iiot.domain.repositories.SensorRepository;

public class AddConditionToRuleGroupUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final SensorRepository sensorRepository;
    private final RuleConditionRepository ruleConditionRepository;

    public AddConditionToRuleGroupUseCase(
            RuleGroupRepository ruleGroupRepository,
            SensorRepository sensorRepository,
            RuleConditionRepository ruleConditionRepository
    ) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.sensorRepository = sensorRepository;
        this.ruleConditionRepository = ruleConditionRepository;
    }

    public RuleCondition execute(AddConditionToRuleGroupInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.ruleGroupId() == null) {
            throw new DomainValidationException("RuleGroup ID cannot be null.");
        }
        if (input.sensorId() == null) {
            throw new DomainValidationException("Sensor ID cannot be null.");
        }
        if (input.operator() == null) {
            throw new DomainValidationException("Comparison operator cannot be null.");
        }
        if (input.thresholdValue() == null) {
            throw new DomainValidationException("Threshold value cannot be null.");
        }

        RuleGroup ruleGroup = ruleGroupRepository.findById(input.ruleGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("RuleGroup", input.ruleGroupId()));

        Sensor sensor = sensorRepository.findById(input.sensorId())
                .orElseThrow(() -> new ResourceNotFoundException("Sensor", input.sensorId()));

        if (sensor.getEquipment() == null ||
                ruleGroup.getEquipment() == null ||
                !sensor.getEquipment().getEquipmentId().equals(ruleGroup.getEquipment().getEquipmentId())) {
            throw new DomainValidationException("Sensor does not belong to the same equipment as the RuleGroup.");
        }

        RuleCondition newCondition = new RuleCondition(
                ruleGroup,
                sensor,
                input.operator(),
                input.thresholdValue()
        );

        return ruleConditionRepository.save(newCondition);
    }
}
