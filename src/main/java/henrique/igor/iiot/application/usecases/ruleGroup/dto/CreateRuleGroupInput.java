package henrique.igor.iiot.application.usecases.ruleGroup.dto;

import henrique.igor.iiot.domain.entities.enums.LogicalOperator;

import java.util.UUID;

public record CreateRuleGroupInput(
        String name,
        String description,
        UUID equipmentId,
        LogicalOperator operatorType,
        Boolean isActive
) {
}
