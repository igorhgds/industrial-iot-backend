package henrique.igor.iiot.infrastructure.web.dtos.alert;

import henrique.igor.iiot.application.usecases.alert.dto.AcknowledgeAlertInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.util.UUID;

@Schema(description = "Payload for acknowledging an anomaly Alert")
public record AcknowledgeAlertRequest(
        @Schema(description = "Identifier of the user acknowledging the alert", example = "123e4567-e89b-12d3-a456-426614174000")
        @NotNull(message = "User ID is required")
        UUID userId
) {
    public AcknowledgeAlertInput toInput(UUID alertId) {
        return new AcknowledgeAlertInput(alertId, userId);
    }
}
