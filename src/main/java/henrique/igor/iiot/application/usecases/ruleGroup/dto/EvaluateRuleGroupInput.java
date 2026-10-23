package henrique.igor.iiot.application.usecases.ruleGroup.dto;

import java.math.BigDecimal;
import java.util.Map;
import java.util.UUID;

public record EvaluateRuleGroupInput(
        UUID ruleGroupId,
        Map<UUID, BigDecimal> sensorReadings
) {
}
