package henrique.igor.iiot.infrastructure.messaging.dto;

import java.math.BigDecimal;
import java.util.UUID;

public record AlertSensorSnapshotEventDTO(
        UUID sensorId,
        String sensorCode,
        BigDecimal valueAtTrigger
) {
}
