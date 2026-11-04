package henrique.igor.iiot.infrastructure.web.dtos.ruleGroup;

import henrique.igor.iiot.application.usecases.ruleGroup.dto.CreateRuleGroupInput;
import henrique.igor.iiot.domain.entities.enums.LogicalOperator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for creating a new dynamic Rule Group")
public record CreateRuleGroupRequest(
        @Schema(description = "Name of the rule group (unique per equipment)", example = "Sobrecarga Térmica e Vibração")
        @NotBlank(message = "Rule group name is required")
        @Size(max = 100, message = "Rule group name must not exceed 100 characters")
        String name,

        @Schema(description = "Detailed description of the anomaly condition evaluated by this rule group", example = "Dispara alerta crítico quando temperatura > 85°C E vibração > 4.5 mm/s")
        String description,

        @Schema(description = "Identifier of the monitored equipment", example = "770e8400-e29b-41d4-a716-446655440222")
        @NotNull(message = "Equipment ID is required")
        UUID equipmentId,

        @Schema(description = "Logical operator used to combine conditions in this group", example = "AND")
        @NotNull(message = "Logical operator type is required")
        LogicalOperator operatorType
) {
    public CreateRuleGroupInput toInput() {
        return new CreateRuleGroupInput(name, description, equipmentId, operatorType, false);
    }
}
