package henrique.igor.iiot.infrastructure.web.dtos.alert;

import henrique.igor.iiot.application.usecases.alert.dto.SensorSnapshotInput;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotNull;

import java.math.BigDecimal;
import java.util.UUID;

@Schema(description = "Sensor reading snapshot captured at the moment an alert is triggered")
public record SensorSnapshotRequest(
        @Schema(description = "Identifier of the sensor", example = "880e8400-e29b-41d4-a716-446655440333")
        @NotNull(message = "Sensor ID is required")
        UUID sensorId,

        @Schema(description = "Measured value at the moment of the threshold breach", example = "88.50")
        @NotNull(message = "Value at trigger is required")
        BigDecimal valueAtTrigger
) {
    public SensorSnapshotInput toInput() {
        return new SensorSnapshotInput(sensorId, valueAtTrigger);
    }
}
