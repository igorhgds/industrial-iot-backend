package henrique.igor.iiot.infrastructure.web.dtos.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.AddConditionToRuleGroupInput;
import henrique.igor.iiot.domain.entities.enums.ComparisonOperator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Payload for adding a sensor threshold condition to a Rule Group")
public record AddConditionToRuleGroupRequest(
        @Schema(description = "Identifier of the sensor to evaluate (must belong to the same equipment as the rule group)", example = "880e8400-e29b-41d4-a716-446655440333")
        @NotNull(message = "Sensor ID is required")
        UUID sensorId,

        @Schema(description = "Comparison operator", example = "GREATER_THAN")
        @NotNull(message = "Comparison operator is required")
        ComparisonOperator operator,

        @Schema(description = "Numeric threshold value for comparison", example = "85.00")
        @NotNull(message = "Threshold value is required")
        BigDecimal thresholdValue
) {
    public AddConditionToRuleGroupInput toInput(UUID ruleGroupId) {
        return new AddConditionToRuleGroupInput(ruleGroupId, sensorId, operator, thresholdValue);
    }
}
