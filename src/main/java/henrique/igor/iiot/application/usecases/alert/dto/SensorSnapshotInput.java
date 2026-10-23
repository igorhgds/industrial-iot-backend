package henrique.igor.iiot.application.usecases.alert.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record SensorSnapshotInput(
        UUID sensorId,
        BigDecimal valueAtTrigger
) {
}
