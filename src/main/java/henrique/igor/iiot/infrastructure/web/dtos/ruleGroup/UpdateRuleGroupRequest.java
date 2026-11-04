package henrique.igor.iiot.infrastructure.web.dtos.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.UpdateRuleGroupInput;
import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import henrique.igor.iiot.domain.entities.enums.Severity;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for updating an existing dynamic Rule Group")
public record UpdateRuleGroupRequest(
        @Schema(description = "Updated name of the rule group", example = "Sobrecarga Térmica Crítica")
        @Size(max = 100, message = "Rule group name must not exceed 100 characters")
        String name,

        @Schema(description = "Updated description of the rule group", example = "Monitoramento de superaquecimento do estator")
        String description,

        @Schema(description = "Alert severity triggered when this rule group breaches", example = "CRITICAL")
        Severity severity,

        @Schema(description = "Logical operator used to combine conditions", example = "AND")
        LogicalOperator operatorType,

        @Schema(description = "Whether the rule group is active (requires at least one condition to activate)", example = "true")
        Boolean isActive
) {
    public UpdateRuleGroupInput toInput(UUID ruleGroupId) {
        return new UpdateRuleGroupInput(ruleGroupId, name, description, severity, operatorType, isActive);
    }
}
