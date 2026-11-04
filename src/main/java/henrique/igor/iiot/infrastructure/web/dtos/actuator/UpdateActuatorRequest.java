package henrique.igor.iiot.infrastructure.web.dtos.actuator;

import henrique.igor.iiot.application.usecases.actuator.dto.UpdateActuatorInput;
import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for updating an existing IoT actuator")
public record UpdateActuatorRequest(
        @Schema(description = "Operational status of the actuator", example = "ONLINE")
        ActuatorStatus actuatorStatus,

        @Schema(description = "Updated MQTT topic for actuation commands", example = "industry/machinery/MTR-USI-002/command")
        @Size(max = 255, message = "Command MQTT topic must not exceed 255 characters")
        String commandMqttTopic,

        @Schema(description = "Updated MQTT topic for state feedback", example = "industry/machinery/MTR-USI-002/state")
        @Size(max = 255, message = "State MQTT topic must not exceed 255 characters")
        String stateMqttTopic,

        @Schema(description = "New equipment ID to relocate the actuator", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId
) {
    public UpdateActuatorInput toInput(UUID actuatorId) {
        return new UpdateActuatorInput(actuatorId, actuatorStatus, commandMqttTopic, stateMqttTopic, equipmentId);
    }
}
