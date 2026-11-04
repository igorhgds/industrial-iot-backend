package henrique.igor.iiot.infrastructure.web.dtos.automation;

import henrique.igor.iiot.domain.entities.AutomationRule;
import henrique.igor.iiot.domain.entities.enums.ActionType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Closed-loop Automation Rule representation returned by the API")
public record AutomationRuleResponse(
        @Schema(description = "Unique identifier of the automation rule", example = "ee0e8400-e29b-41d4-a716-446655440999")
        UUID automationId,

        @Schema(description = "Name of the automation rule", example = "Desligamento de Emergência por Superaquecimento")
        String name,

        @Schema(description = "Identifier of the monitored input sensor", example = "880e8400-e29b-41d4-a716-446655440333")
        UUID inputSensorId,

        @Schema(description = "Code of the monitored input sensor", example = "TEM-001-MTR-USI-001")
        String inputSensorCode,

        @Schema(description = "Identifier of the associated rule condition", example = "dd0e8400-e29b-41d4-a716-446655440888")
        UUID conditionId,

        @Schema(description = "Numeric threshold value", example = "90.00")
        BigDecimal thresholdValue,

        @Schema(description = "Action type executed when triggered", example = "TOGGLE_ACTUATOR")
        ActionType actionType,

        @Schema(description = "Identifier of the target actuator", example = "990e8400-e29b-41d4-a716-446655440444")
        UUID targetActuatorId,

        @Schema(description = "Code of the target actuator", example = "REL-001-MTR-USI-001")
        String targetActuatorCode,

        @Schema(description = "Whether the automation rule is currently active", example = "true")
        Boolean isActive
) {
    public static AutomationRuleResponse fromDomain(AutomationRule rule) {
        return new AutomationRuleResponse(
                rule.getAutomationId(),
                rule.getName(),
                rule.getInputSensorId() != null ? rule.getInputSensorId().getSensorId() : null,
                rule.getInputSensorId() != null ? rule.getInputSensorId().getCode() : null,
                rule.getCondition() != null ? rule.getCondition().getConditionId() : null,
                rule.getThresholdValue(),
                rule.getActionType(),
                rule.getTargetActuator() != null ? rule.getTargetActuator().getActuatorId() : null,
                rule.getTargetActuator() != null ? rule.getTargetActuator().getCode() : null,
                rule.getActive()
        );
    }
}
