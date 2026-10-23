package henrique.igor.iiot.application.usecases.automation.dto;

import java.util.UUID;

public record ExecuteActuationCommandInput(
        UUID automationRuleId,
        String commandPayload
) {
}
