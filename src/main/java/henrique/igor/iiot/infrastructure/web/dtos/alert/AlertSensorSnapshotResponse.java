package henrique.igor.iiot.infrastructure.web.dtos.alert;

import henrique.igor.iiot.domain.entities.AlertSensorSnapshot;
import henrique.igor.iiot.domain.entities.enums.SensorType;
import io.swagger.v3.oas.annotations.media.Schema;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Sensor snapshot representation inside an anomaly Alert")
public record AlertSensorSnapshotResponse(
        @Schema(description = "Unique identifier of the snapshot", example = "ff0e8400-e29b-41d4-a716-446655440000")
        UUID snapshotId,

        @Schema(description = "Identifier of the sensor", example = "880e8400-e29b-41d4-a716-446655440333")
        UUID sensorId,

        @Schema(description = "Code of the sensor", example = "TEM-001-MTR-USI-001")
        String sensorCode,

        @Schema(description = "Type of the sensor", example = "TEMPERATURE")
        SensorType sensorType,

        @Schema(description = "Unit of measure", example = "°C")
        String unitOfMeasure,

        @Schema(description = "Measured value at trigger time", example = "88.50")
        BigDecimal valueAtTrigger
) {
    public static AlertSensorSnapshotResponse fromDomain(AlertSensorSnapshot snapshot) {
        return new AlertSensorSnapshotResponse(
                snapshot.getSnapshotId(),
                snapshot.getSensor() != null ? snapshot.getSensor().getSensorId() : null,
                snapshot.getSensor() != null ? snapshot.getSensor().getCode() : null,
                snapshot.getSensor() != null ? snapshot.getSensor().getSensorType() : null,
                snapshot.getSensor() != null ? snapshot.getSensor().getUnitOfMeasure() : null,
                snapshot.getValueAtTrigger()
        );
    }
}
