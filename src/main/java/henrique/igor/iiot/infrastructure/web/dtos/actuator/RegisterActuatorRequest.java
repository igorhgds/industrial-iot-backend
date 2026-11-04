package henrique.igor.iiot.infrastructure.web.dtos.actuator;

import henrique.igor.iiot.application.usecases.actuator.dto.RegisterActuatorInput;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for registering a new IoT actuator")
public record RegisterActuatorRequest(
        @Schema(
                description = "Optional custom actuator code. If omitted or blank, a code is generated automatically",
                example = "REL-001-MTR-USI-001"
        )
        @Size(max = 50, message = "Actuator code must not exceed 50 characters")
        String code,

        @Schema(description = "Type of the actuator", example = "RELAY")
        @NotNull(message = "Actuator type is required")
        ActuatorType type,

        @Schema(description = "MQTT topic used to publish actuation commands", example = "industry/machinery/MTR-USI-001/command")
        @Size(max = 255, message = "Command MQTT topic must not exceed 255 characters")
        String commandMqttTopic,

        @Schema(description = "MQTT topic used to receive actuator state feedback", example = "industry/machinery/MTR-USI-001/state")
        @Size(max = 255, message = "State MQTT topic must not exceed 255 characters")
        String stateMqttTopic,

        @Schema(description = "Optional equipment ID where the actuator is installed", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId
) {
    public RegisterActuatorInput toInput() {
        return new RegisterActuatorInput(code, type, commandMqttTopic, stateMqttTopic, equipmentId);
    }
}
