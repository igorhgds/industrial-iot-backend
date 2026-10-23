package henrique.igor.iiot.application.usecases.ruleGroup.dto;

import henrique.igor.iiot.domain.entities.enums.ComparisonOperator;

import java.math.BigDecimal;
import java.util.UUID;

public record AddConditionToRuleGroupInput(
        UUID ruleGroupId,
        UUID sensorId,
        ComparisonOperator operator,
        BigDecimal thresholdValue
) {
}
