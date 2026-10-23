package henrique.igor.iiot.application.usecases.telemetry;

import henrique.igor.iiot.application.usecases.alert.TriggerAlertUseCase;
import henrique.igor.iiot.application.usecases.alert.dto.SensorSnapshotInput;
import henrique.igor.iiot.application.usecases.alert.dto.TriggerAlertInput;
import henrique.igor.iiot.application.usecases.ruleGroup.EvaluateRuleGroupUseCase;
import henrique.igor.iiot.application.usecases.ruleGroup.dto.EvaluateRuleGroupInput;
import henrique.igor.iiot.application.usecases.ruleGroup.dto.RuleEvaluationResult;
import henrique.igor.iiot.domain.entities.Equipment;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.enums.HealthStatus;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;

import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

public class ProcessEquipmentAnomaliesUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final EvaluateRuleGroupUseCase evaluateRuleGroupUseCase;
    private final TriggerAlertUseCase triggerAlertUseCase;

    public ProcessEquipmentAnomaliesUseCase(
            RuleGroupRepository ruleGroupRepository,
            EvaluateRuleGroupUseCase evaluateRuleGroupUseCase,
            TriggerAlertUseCase triggerAlertUseCase
    ) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.evaluateRuleGroupUseCase = evaluateRuleGroupUseCase;
        this.triggerAlertUseCase = triggerAlertUseCase;
    }

    public Optional<HealthStatus> execute(Equipment equipment, Map<UUID, BigDecimal> sensorReadings) {
        if (equipment == null || equipment.getEquipmentId() == null) {
            throw new DomainValidationException("Equipment cannot be null.");
        }
        if (sensorReadings == null || sensorReadings.isEmpty()) {
            return Optional.empty();
        }

        List<RuleGroup> ruleGroups = ruleGroupRepository.findByEquipmentId(equipment.getEquipmentId());
        if (ruleGroups.isEmpty()) {
            return Optional.empty();
        }

        HealthStatus calculatedHealth = HealthStatus.HEALTH;
        boolean anyActiveRuleEvaluated = false;

        for (RuleGroup ruleGroup : ruleGroups) {
            if (!Boolean.TRUE.equals(ruleGroup.getActive())) {
                continue;
            }

            anyActiveRuleEvaluated = true;
            RuleEvaluationResult result = evaluateRuleGroupUseCase.execute(
                    new EvaluateRuleGroupInput(ruleGroup.getRuleGroupId(), sensorReadings)
            );

            if (result.triggered()) {
                calculatedHealth = escalateHealthStatus(calculatedHealth, result.calculatedSeverity());

                List<SensorSnapshotInput> snapshotInputs = result.sensorSnapshots().stream()
                        .map(s -> new SensorSnapshotInput(s.getSensor().getSensorId(), s.getValueAtTrigger()))
                        .toList();

                String message = "Anomaly detected by rule group '" + ruleGroup.getName()
                        + "' on equipment " + equipment.getEquipCode();

                triggerAlertUseCase.execute(new TriggerAlertInput(
                        equipment.getEquipmentId(),
                        ruleGroup.getRuleGroupId(),
                        result.calculatedSeverity(),
                        message,
                        snapshotInputs
                ));
            }
        }

        return anyActiveRuleEvaluated ? Optional.of(calculatedHealth) : Optional.empty();
    }

    private HealthStatus escalateHealthStatus(HealthStatus current, Severity severity) {
        if (severity == Severity.CRITICAL) {
            return HealthStatus.CRITICAL;
        }
        if (current != HealthStatus.CRITICAL && (severity == Severity.WARNING || severity == Severity.INFO)) {
            return HealthStatus.WARNING;
        }
        return current;
    }
}
