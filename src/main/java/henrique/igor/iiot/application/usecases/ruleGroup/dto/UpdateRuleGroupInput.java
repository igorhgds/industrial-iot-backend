package henrique.igor.iiot.application.usecases.ruleGroup.dto;

import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;

import java.util.UUID;

public record UpdateRuleGroupInput(
        UUID ruleGroupId,
        String name,
        String description,
        Severity severity,
        LogicalOperator operatorType,
        Boolean isActive
) {
}
