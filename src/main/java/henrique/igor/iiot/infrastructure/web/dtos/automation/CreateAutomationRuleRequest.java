package henrique.igor.iiot.infrastructure.web.dtos.automation;

import henrique.igor.iiot.application.usecases.automation.dto.CreateAutomationRuleInput;
import henrique.igor.iiot.domain.entities.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Payload for creating a new closed-loop Automation Rule")
public record CreateAutomationRuleRequest(
        @Schema(description = "Name of the automation rule", example = "Desligamento de Emergência por Superaquecimento")
        @NotBlank(message = "Automation rule name is required")
        @Size(max = 50, message = "Automation rule name must not exceed 50 characters")
        String name,

        @Schema(description = "Identifier of the input sensor monitored by the rule", example = "880e8400-e29b-41d4-a716-446655440333")
        @NotNull(message = "Input sensor ID is required")
        UUID inputSensorId,

        @Schema(description = "Identifier of the associated rule condition", example = "dd0e8400-e29b-41d4-a716-446655440888")
        @NotNull(message = "Condition ID is required")
        UUID conditionId,

        @Schema(description = "Threshold value that triggers the actuation", example = "90.00")
        @NotNull(message = "Threshold value is required")
        BigDecimal thresholdValue,

        @Schema(description = "Action type to execute when triggered", example = "TOGGLE_ACTUATOR")
        @NotNull(message = "Action type is required")
        ActionType actionType,

        @Schema(description = "Identifier of the target actuator that receives the command", example = "990e8400-e29b-41d4-a716-446655440444")
        @NotNull(message = "Target actuator ID is required")
        UUID targetActuatorId,

        @Schema(description = "Whether the automation rule starts active", example = "true")
        Boolean isActive
) {
    public CreateAutomationRuleInput toInput() {
        return new CreateAutomationRuleInput(
                name,
                inputSensorId,
                conditionId,
                thresholdValue,
                actionType,
                targetActuatorId,
                isActive
        );
    }
}
