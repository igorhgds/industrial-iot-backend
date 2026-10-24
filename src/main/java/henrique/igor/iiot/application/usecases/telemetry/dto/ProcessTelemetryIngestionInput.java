package henrique.igor.iiot.application.usecases.telemetry.dto;

import java.time.OffsetDateTime;
import java.util.List;

public record ProcessTelemetryIngestionInput(
        String equipCode,
        String gatewayCode,
        OffsetDateTime timestamp,
        List<TelemetryReadingInput> readings,
        String rawPayload
) {
}
