package henrique.igor.iiot.infrastructure.messaging.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record TelemetryPayloadDTO(
        String equipCode,
        String gatewayCode,
        OffsetDateTime timestamp,
        List<ReadingDTO> readings
) {
}
