package henrique.igor.iiot.infrastructure.web.dtos.automation;

import henrique.igor.iiot.application.usecases.automation.dto.ExecuteActuationCommandInput;
import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

@Schema(description = "Payload for dispatching an actuation command via MQTT")
public record ExecuteActuationCommandRequest(
        @Schema(
                description = "Optional custom command payload to publish to the actuator MQTT topic. Defaults to the rule actionType name if omitted",
                example = "{\"command\":\"EMERGENCY_STOP\",\"reason\":\"Thermal threshold exceeded\"}"
        )
        String commandPayload
) {
    public ExecuteActuationCommandInput toInput(UUID automationRuleId) {
        return new ExecuteActuationCommandInput(automationRuleId, commandPayload);
    }
}
