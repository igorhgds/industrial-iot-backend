package henrique.igor.iiot.application.usecases.automation.dto;

import henrique.igor.iiot.domain.entities.enums.ActionType;

import java.time.OffsetDateTime;
import java.util.UUID;

public record ActuationCommandResult(
        UUID automationId,
        String actuatorCode,
        String topic,
        ActionType actionType,
        String payload,
        OffsetDateTime executedAt
) {
}
