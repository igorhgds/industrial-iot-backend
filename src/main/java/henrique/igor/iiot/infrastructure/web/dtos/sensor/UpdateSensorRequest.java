package henrique.igor.iiot.infrastructure.web.dtos.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.UpdateSensorInput;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for updating an existing IoT sensor")
public record UpdateSensorRequest(
        @Schema(description = "Operational status of the sensor", example = "ONLINE")
        SensorStatus status,

        @Schema(description = "Updated MQTT topic associated with the sensor", example = "industry/machinery/MTR-USI-002")
        @Size(max = 100, message = "MQTT topic must not exceed 100 characters")
        String mqttTopic,

        @Schema(description = "New equipment ID to relocate the sensor", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId
) {
    public UpdateSensorInput toInput(UUID sensorId) {
        return new UpdateSensorInput(sensorId, status, mqttTopic, equipmentId);
    }
}
