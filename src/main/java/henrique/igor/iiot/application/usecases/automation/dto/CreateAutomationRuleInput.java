package henrique.igor.iiot.application.usecases.automation.dto;

import henrique.igor.iiot.domain.entities.enums.ActionType;

import java.math.BigDecimal;
import java.util.UUID;

public record CreateAutomationRuleInput(
        String name,
        UUID inputSensorId,
        UUID conditionId,
        BigDecimal thresholdValue,
        ActionType actionType,
        UUID targetActuatorId,
        Boolean isActive
) {
}
