package henrique.igor.iiot.application.usecases.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.EvaluateRuleGroupInput;
import henrique.igor.iiot.application.usecases.ruleGroup.dto.RuleEvaluationResult;
import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;
import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.RuleGroup;
import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;
import henrique.igor.iiot.domain.exceptions.DomainValidationException;
import henrique.igor.iiot.domain.exceptions.ResourceNotFoundException;
import henrique.igor.iiot.domain.repositories.RuleConditionRepository;
import henrique.igor.iiot.domain.repositories.RuleGroupRepository;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.UUID;

public class EvaluateRuleGroupUseCase {

    private final RuleGroupRepository ruleGroupRepository;
    private final RuleConditionRepository ruleConditionRepository;

    public EvaluateRuleGroupUseCase(
            RuleGroupRepository ruleGroupRepository,
            RuleConditionRepository ruleConditionRepository
    ) {
        this.ruleGroupRepository = ruleGroupRepository;
        this.ruleConditionRepository = ruleConditionRepository;
    }

    public RuleEvaluationResult execute(EvaluateRuleGroupInput input) {
        if (input == null) {
            throw new DomainValidationException("Input payload cannot be null.");
        }
        if (input.ruleGroupId() == null) {
            throw new DomainValidationException("RuleGroup ID cannot be null.");
        }
        if (input.sensorReadings() == null || input.sensorReadings().isEmpty()) {
            throw new DomainValidationException("Sensor readings cannot be null or empty.");
        }

        RuleGroup ruleGroup = ruleGroupRepository.findById(input.ruleGroupId())
                .orElseThrow(() -> new ResourceNotFoundException("RuleGroup", input.ruleGroupId()));

        if (!Boolean.TRUE.equals(ruleGroup.getActive())) {
            return new RuleEvaluationResult(ruleGroup, false, Severity.REGULAR, List.of(), List.of());
        }

        List<RuleCondition> conditions = ruleConditionRepository.findByRuleGroupId(input.ruleGroupId());
        if (conditions.isEmpty()) {
            return new RuleEvaluationResult(ruleGroup, false, Severity.REGULAR, List.of(), List.of());
        }

        List<RuleCondition> breachedConditions = new ArrayList<>();
        List<AlertSensorSnapshot> snapshots = new ArrayList<>();

        for (RuleCondition condition : conditions) {
            UUID sensorId = condition.getSensor().getSensorId();
            BigDecimal readingValue = input.sensorReadings().get(sensorId);

            if (readingValue != null) {
                snapshots.add(new AlertSensorSnapshot(condition.getSensor(), readingValue));
                if (condition.evaluate(readingValue)) {
                    breachedConditions.add(condition);
                }
            }
        }

        boolean triggered = ruleGroup.getOperatorType() == LogicalOperator.AND
                ? breachedConditions.size() == conditions.size()
                : !breachedConditions.isEmpty();

        if (!triggered) {
            return new RuleEvaluationResult(ruleGroup, false, Severity.REGULAR, List.of(), List.of());
        }

        Severity severity = calculateSeverity(ruleGroup, breachedConditions, input.sensorReadings());

        return new RuleEvaluationResult(
                ruleGroup,
                true,
                severity,
                breachedConditions,
                snapshots
        );
    }

    private Severity calculateSeverity(
            RuleGroup ruleGroup,
            List<RuleCondition> breachedConditions,
            Map<UUID, BigDecimal> readings
    ) {
        if (ruleGroup.getSeverity() != null && ruleGroup.getSeverity() != Severity.REGULAR) {
            return ruleGroup.getSeverity();
        }

        BigDecimal maxDeviationRatio = BigDecimal.ZERO;
        for (RuleCondition condition : breachedConditions) {
            BigDecimal reading = readings.get(condition.getSensor().getSensorId());
            BigDecimal threshold = condition.getThresholdValue();
            if (reading != null && threshold != null && threshold.compareTo(BigDecimal.ZERO) != 0) {
                BigDecimal diff = reading.subtract(threshold).abs();
                BigDecimal ratio = diff.divide(threshold.abs(), 4, RoundingMode.HALF_UP);
                if (ratio.compareTo(maxDeviationRatio) > 0) {
                    maxDeviationRatio = ratio;
                }
            }
        }

        if (maxDeviationRatio.compareTo(new BigDecimal("0.20")) >= 0) {
            return Severity.CRITICAL;
        }
        if (maxDeviationRatio.compareTo(new BigDecimal("0.10")) >= 0) {
            return Severity.WARNING;
        }
        return Severity.INFO;
    }
}
