package henrique.igor.iiot.infrastructure.web.dtos.actuator;

import henrique.igor.iiot.domain.entities.Actuator;
import henrique.igor.iiot.domain.entities.enums.ActuatorStatus;
import henrique.igor.iiot.domain.entities.enums.ActuatorType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "IoT actuator representation returned by the API")
public record ActuatorResponse(
        @Schema(description = "Unique identifier of the actuator", example = "990e8400-e29b-41d4-a716-446655440444")
        UUID actuatorId,

        @Schema(description = "Unique business code of the actuator", example = "REL-001-MTR-USI-001")
        String code,

        @Schema(description = "Type of the actuator", example = "RELAY")
        ActuatorType actuatorType,

        @Schema(description = "Current operational status", example = "ONLINE")
        ActuatorStatus status,

        @Schema(description = "MQTT topic used to publish actuation commands", example = "industry/machinery/MTR-USI-001/command")
        String commandMqttTopic,

        @Schema(description = "MQTT topic used to receive actuator state feedback", example = "industry/machinery/MTR-USI-001/state")
        String stateMqttTopic,

        @Schema(description = "Identifier of the equipment where the actuator is installed", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId,

        @Schema(description = "Code of the equipment where the actuator is installed", example = "MTR-USI-001")
        String equipCode,

        @Schema(description = "Timestamp when the actuator was registered", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static ActuatorResponse fromDomain(Actuator actuator) {
        return new ActuatorResponse(
                actuator.getActuatorId(),
                actuator.getCode(),
                actuator.getActuatorType(),
                actuator.getStatus(),
                actuator.getCommandMqttTopic(),
                actuator.getStateMqttTopic(),
                actuator.getEquipment() != null ? actuator.getEquipment().getEquipmentId() : null,
                actuator.getEquipment() != null ? actuator.getEquipment().getEquipCode() : null,
                actuator.getCreatedAt()
        );
    }
}
