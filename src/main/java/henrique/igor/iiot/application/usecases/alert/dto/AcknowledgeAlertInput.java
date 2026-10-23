package henrique.igor.iiot.application.usecases.alert.dto;

import java.util.UUID;

public record AcknowledgeAlertInput(
        UUID alertId,
        UUID userId
) {
}
