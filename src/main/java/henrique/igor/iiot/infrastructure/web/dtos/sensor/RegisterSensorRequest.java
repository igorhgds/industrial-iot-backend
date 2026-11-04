package henrique.igor.iiot.infrastructure.web.dtos.sensor;

import henrique.igor.iiot.application.usecases.sensor.dto.RegisterSensorInput;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

import java.util.UUID;

@Schema(description = "Payload for registering a new IoT sensor")
public record RegisterSensorRequest(
        @Schema(
                description = "Optional custom sensor code. If omitted or blank, a code is generated automatically",
                example = "TEM-001-MTR-USI-001"
        )
        @Size(max = 50, message = "Sensor code must not exceed 50 characters")
        String code,

        @Schema(description = "Type of physical metric measured by the sensor", example = "TEMPERATURE")
        @NotNull(message = "Sensor type is required")
        SensorType sensorType,

        @Schema(description = "Unit of measure for the sensor readings", example = "°C")
        @Size(max = 5, message = "Unit of measure must not exceed 5 characters")
        String unitOfMeasure,

        @Schema(description = "MQTT topic associated with the sensor telemetry", example = "industry/machinery/MTR-USI-001")
        @Size(max = 100, message = "MQTT topic must not exceed 100 characters")
        String mqttTopic,

        @Schema(description = "Optional equipment ID where the sensor is attached", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId
) {
    public RegisterSensorInput toInput() {
        return new RegisterSensorInput(code, sensorType, unitOfMeasure, mqttTopic, equipmentId);
    }
}
