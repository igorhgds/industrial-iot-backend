package henrique.igor.iiot.infrastructure.web.dtos.ruleGroup;

import henrique.igor.iiot.domain.entities.RuleCondition;
import henrique.igor.iiot.domain.entities.enums.ComparisonOperator;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Rule Condition representation returned by the API")
public record RuleConditionResponse(
        @Schema(description = "Unique identifier of the rule condition", example = "dd0e8400-e29b-41d4-a716-446655440888")
        UUID conditionId,

        @Schema(description = "Identifier of the parent rule group", example = "cc0e8400-e29b-41d4-a716-446655440777")
        UUID ruleGroupId,

        @Schema(description = "Identifier of the evaluated sensor", example = "880e8400-e29b-41d4-a716-446655440333")
        UUID sensorId,

        @Schema(description = "Code of the evaluated sensor", example = "TEM-001-MTR-USI-001")
        String sensorCode,

        @Schema(description = "Comparison operator", example = "GREATER_THAN")
        ComparisonOperator operator,

        @Schema(description = "Threshold value", example = "85.00")
        BigDecimal thresholdValue
) {
    public static RuleConditionResponse fromDomain(RuleCondition condition) {
        return new RuleConditionResponse(
                condition.getConditionId(),
                condition.getRuleGroup() != null ? condition.getRuleGroup().getRuleGroupId() : null,
                condition.getSensor() != null ? condition.getSensor().getSensorId() : null,
                condition.getSensor() != null ? condition.getSensor().getCode() : null,
                condition.getOperator(),
                condition.getThresholdValue()
        );
    }
}
