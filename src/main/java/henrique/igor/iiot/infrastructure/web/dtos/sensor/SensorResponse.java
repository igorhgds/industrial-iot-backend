package henrique.igor.iiot.infrastructure.web.dtos.sensor;

import henrique.igor.iiot.domain.entities.Sensor;
import henrique.igor.iiot.domain.entities.enums.SensorStatus;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.time.OffsetDateTime;
import java.util.UUID;

@Schema(description = "IoT sensor representation returned by the API")
public record SensorResponse(
        @Schema(description = "Unique identifier of the sensor", example = "880e8400-e29b-41d4-a716-446655440333")
        UUID sensorId,

        @Schema(description = "Unique business code of the sensor", example = "TEM-001-MTR-USI-001")
        String code,

        @Schema(description = "Physical metric measured by the sensor", example = "TEMPERATURE")
        SensorType sensorType,

        @Schema(description = "Current operational status", example = "ONLINE")
        SensorStatus status,

        @Schema(description = "Unit of measure", example = "°C")
        String unitOfMeasure,

        @Schema(description = "MQTT topic associated with the sensor", example = "industry/machinery/MTR-USI-001")
        String mqttTopic,

        @Schema(description = "Identifier of the equipment where the sensor is installed", example = "770e8400-e29b-41d4-a716-446655440222")
        UUID equipmentId,

        @Schema(description = "Code of the equipment where the sensor is installed", example = "MTR-USI-001")
        String equipCode,

        @Schema(description = "Timestamp when the sensor was registered", example = "2026-10-06T12:00:00Z")
        OffsetDateTime createdAt
) {
    public static SensorResponse fromDomain(Sensor sensor) {
        return new SensorResponse(
                sensor.getSensorId(),
                sensor.getCode(),
                sensor.getSensorType(),
                sensor.getStatus(),
                sensor.getUnitOfMeasure(),
                sensor.getMqttTopic(),
                sensor.getEquipment() != null ? sensor.getEquipment().getEquipmentId() : null,
                sensor.getEquipment() != null ? sensor.getEquipment().getEquipCode() : null,
                sensor.getCreatedAt()
        );
    }
}
